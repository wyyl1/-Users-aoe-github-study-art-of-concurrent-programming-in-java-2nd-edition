package com.wyyl1.study.chapter1;

import java.util.concurrent.TimeUnit;

class SleepUtils {
    static void second(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
