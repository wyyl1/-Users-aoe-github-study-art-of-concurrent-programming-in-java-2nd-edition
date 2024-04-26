package com.wyyl1.study.chapter1.connection_pool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class ConnectionPoolTest {

    static ConnectionPool pool = new ConnectionPool(10);
    // 保证所有的 ConnectionRunner 能够同时开始
    static CountDownLatch start = new CountDownLatch(1);
    // main 线程将会等待所有 ConnectionRunner 结束后才能继续执行
    static CountDownLatch end;

    public static void main(String[] args) throws Exception {
        // 线程数量，可以修改线程数量进行观察
        int threadCount = 10;
        end = new CountDownLatch(threadCount);

        final int runMax = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(runMax, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }

        start.countDown();
        end.await();

        System.out.println("total invoke: " + (threadCount * runMax));
        System.out.println("got connection: " + got);
        System.out.println("not got connection: " + notGot);
    }

    static class ConnectionRunner implements Runnable {
        int runMax;
        AtomicInteger got;
        AtomicInteger notGot;

        ConnectionRunner(int runMax, AtomicInteger got, AtomicInteger notGot) {
            this.runMax = runMax;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            while (runMax > 0) {
                try {
                    // 从线程池中获取连接，如果 1000ms 内无法获取到，将会返回 null
                    // 分别统计连接获取的数量 got 和未获取到的数量 notGot
                    Connection connection = pool.fetchConnection(1000);
                    if (connection != null) {
                        try {
                            connection.createStatement();
                            connection.commit();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        notGot.incrementAndGet();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    runMax--;
                }
            }

            end.countDown();
        }
    }
}
