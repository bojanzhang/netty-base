package com.waya.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

public class JavaFutureDemo {
    public static final int SLEEP_GAP = 500;
    static Logger logger = Logger.getLogger("JavaFutureDemo");

    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    static class HotWarterJob implements Callable<Boolean> {  //1
        @Override
        public Boolean call() {// 2
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

    public static void drinkTea(boolean warterOk, boolean cupOk) {
        if (warterOk && cupOk) {
            logger.info("泡茶喝");
        } else if (!warterOk) {
            logger.info("烧水失败，没有茶喝了");
        } else if (!cupOk) {
            logger.info("杯子洗不了，没有茶喝了");
        }
    }

    public static void main(String args[]) {
        Callable<Boolean> hJob = new HotWarterJob();//③
        FutureTask<Boolean> hTask = new FutureTask<>(hJob);//④
        Thread hThread = new Thread(hTask, "** 烧水-Thread");//⑤
        Callable<Boolean> wJob = new WashJob();//③
        FutureTask<Boolean> wTask = new FutureTask<>(wJob);//④
        Thread wThread = new Thread(wTask, "$$ 清洗-Thread");//⑤
        hThread.start();
        wThread.start();
        Thread.currentThread().setName("主线程");
        try {
            boolean warterOk = hTask.get();
            boolean cupOk = wTask.get();
            drinkTea(warterOk, cupOk);
        } catch (InterruptedException e) {
            logger.info(getCurThreadName() + "发生异常被中断.");
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        logger.info(getCurThreadName() + " 运行结束.");
    }
}
