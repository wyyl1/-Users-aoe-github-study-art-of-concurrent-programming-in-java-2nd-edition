package com.wyyl1.study.chapter1;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

class MultiThread {

    public static void main(String[] args) {
        // 获取 Java 线程管理 MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // 不需要获取同步的 monitor 和 synchronizer 信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        // 遍历线程信息，仅打印线程 ID 和线程名称信息
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("[" + threadInfo.getThreadId() + "] " + threadInfo.getThreadName());
        }
    }

    /***
     * Signal Dispatcher 分发和管理 JVM 信号的线程
     * Finalizer 调用对象 finalize 方法的线程
     * Reference Handler 清除 Reference 的线程
     * main 主线程，用户程序入口
     *
     * AI 回答
     * Notification Thread 是 Java 虚拟机（JVM）用于处理 Java EE 应用程序中的异步通知的线程。它负责将异步通知从应用程序服务器传递给应用程序。
     * 例如，当用户向 Web 应用程序提交表单时，Notification Thread 可能用于将表单提交事件通知应用程序。
     *
     * Common-Cleaner 是 Java 9 中引入的新线程。它负责清理废弃的对象。在 Java 8 及更早版本中，这项工作由 Finalizer 线程完成。
     * 但是，Finalizer 线程的性能很差，因为它是单线程的，并且必须等待每个废弃对象的 finalize 方法执行。
     * Common-Cleaner 线程是多线程的，并且可以并行清理废弃的对象。这可以提高清理废弃对象的性能，从而减少内存使用和垃圾回收开销。
     *
     * Notification Thread 和 Common-Cleaner 线程都是守护线程，这意味着它们不会阻止应用程序终止。
     * Notification Thread 和 Common-Cleaner 线程的优先级通常较低，这意味着它们不会竞争 CPU 资源与应用程序的其他线程。
     */
}
