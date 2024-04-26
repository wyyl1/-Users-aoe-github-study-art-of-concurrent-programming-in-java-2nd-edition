package com.wyyl1.study.chapter1.connection_pool;

import java.sql.Connection;
import java.util.LinkedList;

class ConnectionPool {
    private final LinkedList<Connection> pool = new LinkedList<>();

    ConnectionPool(int initialSize) {
        if (initialSize > 0) {
            for (int i = 0; i < initialSize; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                // 释放连接后需要进行通知，这样其他消费者能够感知到连接池中已经归还了一个连接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // 完全超时
            if (mills <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            }

            long future = System.currentTimeMillis() + mills;
            long remaining = mills;

            while (pool.isEmpty() && remaining > 0) {
                pool.wait(remaining);
                remaining = future - System.currentTimeMillis();
            }

            Connection result = null;

            if (!pool.isEmpty()) {
                result = pool.removeFirst();
            }

            return result;
        }
    }
}
