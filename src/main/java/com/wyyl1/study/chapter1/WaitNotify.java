package com.wyyl1.study.chapter1;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WaitNotify {
    static boolean flag = true;
    static final Object lock = new Object();

    public static void main(String[] args) {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();

        SleepUtils.second(1);

        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();

    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                while (flag) {
                    try {
                        print("flag is true.", "wait");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                print("flag is false", "running");
            }
        }
    }

    static class Notify implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                print("hold lock. ", "notify");
                lock.notifyAll();
                flag = false;
                SleepUtils.second(3);
            }

            synchronized (lock) {
                print("hold lock again.", "sleep");
                SleepUtils.second(3);
            }
        }
    }

    private static void print(String say, String state) {
        System.out.println(Thread.currentThread() + " " + say + " " + state + "@ " + now());
    }

    private static String now() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

}
