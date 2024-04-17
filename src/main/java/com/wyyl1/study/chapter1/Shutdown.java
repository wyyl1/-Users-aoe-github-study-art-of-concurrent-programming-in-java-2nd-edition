package com.wyyl1.study.chapter1;

public class Shutdown {

    public static void main(String[] args) {
        Thread countThread = new Thread(new Runner(), "使用 interrupt() 来终止线程");
        countThread.start();
        // 睡眠 1 秒，main 线程对 CountThread 进行中断，使 CountThread 能够感知中断而结束
        SleepUtils.second(1);
        countThread.interrupt();

        Runner runner = new Runner();
        countThread = new Thread(runner, "使用 on 标识来终止线程");
        countThread.start();
        // 睡眠 1 秒，main 线程对 CountThread 进行取消，使 CountThread 能够感知 on 为 false 而结束
        SleepUtils.second(1);
        runner.cancel();
    }

    private static class Runner implements Runnable {
        private long i;
        private volatile boolean on = true;

        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println(Thread.currentThread().getName() + " Count i = " + i);
        }

        public void cancel() {
            on = false;
        }
    }

}
