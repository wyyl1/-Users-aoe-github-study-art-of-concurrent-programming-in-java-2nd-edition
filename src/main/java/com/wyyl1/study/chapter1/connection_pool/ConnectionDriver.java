package com.wyyl1.study.chapter1.connection_pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;

class ConnectionDriver {
    static class ConnectionHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
            if ("commit".equals(method.getName())) {
                Thread.sleep(100);
            }
            return null;
        }
    }

    static Connection createConnection() {
        return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(), new Class<?>[] {Connection.class}, new ConnectionHandler());
    }
}
