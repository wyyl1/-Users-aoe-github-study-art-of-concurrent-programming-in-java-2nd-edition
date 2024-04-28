package com.wyyl1.study.chapter1.thread_pool;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    // 线程池最大限制数
    private static final int MAX_WORKER_NUMBERS = 10;
    // 线程池默认的数量
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    // 线程池最小的数量
    private static final int MIN_WORKER_NUMBERS = 1;
    // 这是一个工作列表，将会向里面插入工作
    private final LinkedList<Job> jobs = new LinkedList<>();
    // 工作者列表
    private final LinkedList<Worker> workers = new LinkedList<>();
    // 线程编号生成
    private final AtomicLong threadNum = new AtomicLong();
    // 工作者线程的数量
    private int workerNum = DEFAULT_WORKER_NUMBERS;

    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);
    }


    @Override
    public void execute(Job job) {
        if (job != null) {
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        removeWorkers(workerNum);
    }

    @Override
    public synchronized void addWorkers(int num) {
        // 限制新增的 Worker 数量不能超过最大值
        if (num + this.workerNum > MAX_WORKER_NUMBERS) {
            num = MAX_WORKER_NUMBERS - this.workerNum;
        }
        initializeWorkers(num);
        this.workerNum += num;
    }

    @Override
    public synchronized void removeWorkers(int num) {
        if (num >= this.workerNum) {
            throw new IllegalArgumentException("beyond workNum");
        }
        // 按照给定的数量停止 Worker
        int count = 0;
        while (count < num) {
            workers.removeFirst().shutdown();
            count++;
        }
        this.workerNum -= count;
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    class Worker implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    // 如果工作列表是空的，那么就 wait
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            // 感知到外部对 WorkerThread 的中断操作，返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // 取出一个 Job
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception e) {
                        // 忽略 Job 执行中的 Exception
                        e.printStackTrace();
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}
