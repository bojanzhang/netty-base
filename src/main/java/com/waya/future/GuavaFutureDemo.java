package com.waya.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class GuavaFutureDemo {
    public static final int SLEEP_GAP = 500;
    static Logger logger = Logger.getLogger("GuavaFutureDemo");

    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    //业务逻辑：烧水
    static class HotWarterJob implements Callable<Boolean> {
        @Override
        public Boolean call() {
            try {
                logger.info("洗好水壶");
                logger.info("灌上凉水");
                logger.info("放在火上");
                //线程睡眠一段时间，代表烧水中
                Thread.sleep(SLEEP_GAP);
                logger.info("水开了");
            } catch (InterruptedException e) {
                logger.info(" 发生异常被中断.");
                return false;
            }
            logger.info(" 运行结束.");
            return true;
        }
    }

    //业务逻辑：清洗
    static class WashJob implements Callable<Boolean> {
        @Override
        public Boolean call() {
            try {
                logger.info("洗茶壶");
                logger.info("洗茶杯");
                logger.info("拿茶叶");
                //线程睡眠一段时间，代表清洗中
                Thread.sleep(SLEEP_GAP);
                logger.info("洗完了");
            } catch (InterruptedException e) {
                logger.info(" 清洗工作发生异常被中断.");
                return false;
            }
            logger.info(" 清洗工作运行结束.");
            return true;
        }
    }

    //新创建一个异步业务类型，作为泡茶喝主线程类
    static class MainJob implements Runnable {
        boolean warterOk = false;
        boolean cupOk = false;
        int gap = SLEEP_GAP / 10;

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(gap);
                    logger.info("读书中......");
                } catch (InterruptedException e) {
                    logger.info(getCurThreadName() + "发生异常被中断.");
                }
                if (warterOk && cupOk) {
                    drinkTea(warterOk, cupOk);
                }
            }
        }

        public void drinkTea(Boolean wOk, Boolean cOK) {
            if (wOk && cOK) {
                logger.info("泡茶喝，茶喝完");
                this.warterOk = false;
                this.gap = SLEEP_GAP * 100;
            } else if (!wOk) {
                logger.info("烧水失败，没有茶喝了");
            } else if (!cOK) {
                //杯子洗不了没有茶喝了
                logger.info("杯子洗不了，没有茶喝了");
            }
        }
    }

    public static void main(String args[]) {
        //创建一个新的线程实例，作为泡茶主线程
        MainJob mainJob = new MainJob();
        Thread mainThread = new Thread(mainJob);
        mainThread.setName("主线程");
        mainThread.start();
        //烧水的业务逻辑实例
        Callable<Boolean> hotJob = new HotWarterJob();
        //清洗的业务逻辑实例
        Callable<Boolean> washJob = new WashJob();
        //创建Java 线程池
        ExecutorService jPool = Executors.newFixedThreadPool(10);
        //包装Java线程池，构造Guava 线程池
        ListeningExecutorService gPool
                = MoreExecutors.listeningDecorator(jPool);
        //提交烧水的业务逻辑实例，到Guava线程池获取异步任务
        ListenableFuture<Boolean> hotFuture = gPool.submit(hotJob);
        //绑定异步回调，烧水完成后，把喝水任务的warterOk标志设置为true
        Futures.addCallback(hotFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean result) {
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, gPool);
        //提交清洗的业务逻辑实例，到Guava线程池获取异步任务
        ListenableFuture<Boolean> washFuture = gPool.submit(washJob);
        //绑定任务执行完成后的回调逻辑到异步任务
        Futures.addCallback(washFuture, new FutureCallback<Boolean>() {
            public void onSuccess(Boolean r) {
                if (r) {
                    mainJob.cupOk = true;
                }
            }

            public void onFailure(Throwable t) {
                logger.info("杯子洗不了，没有茶喝了");
            }
        }, gPool);
    }
}
