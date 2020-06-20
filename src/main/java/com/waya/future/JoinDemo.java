package com.waya.future;

import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 *
 *
 * @param
 * @return
 */
public class JoinDemo {
    static Logger logger = Logger.getLogger("JoinDemo");
    public static final int SLEEP_GAP = 500;

    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    static class HotWaterThread extends Thread {
        public HotWaterThread() {
            super("** 烧水-Thread");
        }

        public void run() {
            try {
                logger.info("洗好水壶");
                logger.info("灌上凉水");
                logger.info("放在火上");
                //线程睡眠一段时间，代表烧水中
                Thread.sleep(SLEEP_GAP);
                logger.info("水开了");
            } catch (InterruptedException e) {
                logger.info(" 发生异常被中断.");
            }
            logger.info(this.getName() + " 运行结束.");
        }
    }

    static class WashThread extends Thread {
        public WashThread() {
            super("$$ 清洗-Thread");
        }

        public void run() {
            try {
                logger.info("洗茶壶");
                logger.info("洗茶杯");
                logger.info("拿茶叶");
                //线程睡眠一段时间，代表清洗中
                Thread.sleep(SLEEP_GAP);
                logger.info("洗完了");
            } catch (InterruptedException e) {
                logger.info(" 发生异常被中断.");
            }
            logger.info(this.getName() + " 运行结束.");
        }
    }

    public static void main(String args[]) {
        Thread hThread = new HotWaterThread();
        Thread wThread = new WashThread();
        hThread.start();
        wThread.start();
//        FutureTask
        try {
            // 合并烧水-线程
            hThread.join();
            // 合并清洗-线程
            wThread.join();
            Thread.currentThread().setName("主线程");
            logger.info("泡茶喝");
        } catch (InterruptedException e) {
            logger.info(getCurThreadName() + "发生异常被中断.");
        }
        logger.info(getCurThreadName() + " 运行结束.");
    }
}
