package com.wyyl1.study.chapter1;

public class ThreadLocalProfiler {

    private static final ThreadLocal<Long> TIME_THREADLOCAL = ThreadLocal.withInitial(System::currentTimeMillis);

    static void begin() {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    static long costTime() {
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }

    public static void main(String[] args) {
        ThreadLocalProfiler.begin();
        SleepUtils.second(1);
        System.out.println("Cost: " + ThreadLocalProfiler.costTime() + " mills");
    }
}
