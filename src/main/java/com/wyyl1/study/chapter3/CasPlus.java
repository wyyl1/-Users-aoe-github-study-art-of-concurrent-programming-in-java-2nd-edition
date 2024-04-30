package com.wyyl1.study.chapter3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CasPlus {

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        CasPlus cas = new CasPlus();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    cas.safeCount();
                }
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int result = cas.atomicInteger.get();

        if (result != 10000) {
            throw new AssertionError("result is not 10000");
        }

        System.out.println("result:" + result);
    }

    private void safeCount() {
        atomicInteger.addAndGet(1);
    }
}
