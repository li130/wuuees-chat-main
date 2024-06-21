package com.wuuees.chat.common.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 */
public class ThreadPoolUtil {

    private static final int CORE_POOL_SIZE = 5; // 核心线程数

    private static final int MAX_POOL_SIZE = 10; // 最大线程数

    private static final long KEEP_ALIVE_TIME = 1L; // 非核心线程空闲时存活时间

    private static final TimeUnit UNIT = TimeUnit.MINUTES; // 时间单位

    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(100); // 任务队列

    private final ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolUtil() {
        this.threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                UNIT,
                WORK_QUEUE
        );
    }

    /**
     * 提交任务到线程池
     *
     * @param task 任务
     */
    public void submit(Runnable task) {
        this.threadPoolExecutor.submit(task);
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        this.threadPoolExecutor.shutdown();
        try {
            if (!this.threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                this.threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPoolExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static ThreadPoolUtil me() {
        return Holder.ME;
    }

    /**
     * 通过 JVM 的类加载机制, 保证只加载一次 (singleton)
     */
    private static class Holder {
        static final ThreadPoolUtil ME = new ThreadPoolUtil();
    }
}
