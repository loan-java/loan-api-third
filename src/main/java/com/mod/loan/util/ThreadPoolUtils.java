package com.mod.loan.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 50线程大小的线程池
 */
public class ThreadPoolUtils {

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            100,       // 核心池的大小
            100,   // 线程池最大线程数
            0L,       // 表示线程没有任务执行时最多保持多久时间会终止。
            TimeUnit.MILLISECONDS,  // 参数keepAliveTime的时间单位
            new LinkedBlockingQueue<>()); // 线程等待队列, 无界
}
