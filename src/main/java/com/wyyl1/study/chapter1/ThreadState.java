package com.wyyl1.study.chapter1;

class ThreadState {

    public static void main(String[] args) {
        new Thread(new TimeWaiting(), "TimeWaitingThread").start();
        new Thread(new Waiting(), "WaitingThread").start();
        // 使用两个 Blocked 线程，一个获取锁成功，另一个被阻塞
        new Thread(new Blocked(), "BlockedThread-1").start();
        new Thread(new Blocked(), "BlockedThread-2").start();
    }

    // 该线程不断的睡眠
    static class TimeWaiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                sleep100Seconds();
            }
        }
    }

    // 该线程在 Waiting.class 实例上等待
    static class Waiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (Waiting.class) {
                    try {
                        Waiting.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 该线程在 Blocked.class 实例上加锁后，不会释放该锁
    static class Blocked implements Runnable {
        @Override
        public void run() {
            synchronized (Blocked.class) {
                sleep100Seconds();
            }
        }
    }

    private static void sleep100Seconds() {
        SleepUtils.second(100);
    }
}

/***
 * 使用 jps 命令查看当前运行的 Java 进程
 * 使用 jstack 命令查看线程堆栈信息，需要快速敲命令，不然 BlockedThread-1 就挂了
 * AI 分析结果
 *
 * TimeWaitingThread (线程 14): 处于 TIMED_WAITING (sleeping) 状态。 这意味着该线程正在调用 sleep 方法并等待一段时间。
 * WaitingThread (线程 15): 处于 WAITING (on object monitor) 状态。 这意味着该线程正在等待某个对象的监视器 (monitor) 被释放。
 * BlockedThread-1 (16): 同样处于TIMED_WAITING状态，也是因为调用了Thread.sleep()。
 * BlockedThread-2 (线程 17): 处于 TIMED_WAITING (sleeping) 状态。 这意味着该线程正在调用 sleep 方法并等待一段时间。
 */