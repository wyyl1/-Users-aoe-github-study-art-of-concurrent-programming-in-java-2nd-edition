package com.wyyl1.study.chapter1;

class Interrupted {

    public static void main(String[] args) {
        // sleepThread 不停的尝试睡眠
        Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
        sleepThread.setDaemon(true);
        sleepThread.start();

        // busyThread 不停的运行
        Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
        busyThread.setDaemon(true);
        busyThread.start();

        // 休眠 5 秒，让 sleepThread 和 busyThread 充分运行
        SleepUtils.second(5);

        sleepThread.interrupt();
        busyThread.interrupt();

        System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
        System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());

        // 防止 sleepThread 和 busyThread 立刻退出
        SleepUtils.second(2);
    }

    static class SleepRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
                SleepUtils.second(10);
            }
        }
    }

    static class BusyRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
            }
        }
    }
}

/**
 * interrupt() 有什么作用？有哪些应用场景？
 * 作用是向线程发送一个中断信号，这个信号告诉线程应该中断正在进行的操作。当线程接收到这个信号后，可以采取适当的方式来响应中断。
 * interrupt()方法并不会立即停止线程。它仅仅是设置了线程的中断状态位，至于线程如何响应中断，需要开发者在代码中明确指定。
 *
 * 应用场景
 * 取消长时间运行的任务
 * 正确地处理长时间的等待状态
 *
 *
 * ```cmd
 * > Task :Interrupted.main()
 * java.lang.InterruptedException: sleep interrupted
 * 	at java.base/java.lang.Thread.sleep(Native Method)
 * 	at java.base/java.lang.Thread.sleep(Thread.java:337)
 * 	at java.base/java.util.concurrent.TimeUnit.sleep(TimeUnit.java:446)
 * 	at com.wyyl1.study.chapter1.SleepUtils.second(SleepUtils.java:8)
 * 	at com.wyyl1.study.chapter1.Interrupted$SleepRunner.run(Interrupted.java:33)
 * 	at java.base/java.lang.Thread.run(Thread.java:833)
 * SleepThread interrupted is false
 * BusyThread interrupted is true
 * ```
 *
 * 为什么 SleepThread 没有被中断？
 * SleepThread 确实被中断过。这是由InterruptedException的抛出证实的，这个异常在SleepUtils.second(10)内部的Thread.sleep()方法调用时被抛出，因为sleepThread在那时收到了中断信号。
 * 关键点在于对于sleepThread.isInterrupted()调用返回false。在SleepRunner内的run方法中，当Thread.sleep()因为中断而抛出InterruptedException时，JVM会清除中断状态。
 * 这意味着当异常被抛出后，sleepThread的中断状态会被重置为默认的false状态。
 */