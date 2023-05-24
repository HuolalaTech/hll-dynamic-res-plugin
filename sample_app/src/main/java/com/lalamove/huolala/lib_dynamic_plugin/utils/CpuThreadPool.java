package com.lalamove.huolala.lib_dynamic_plugin.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: CpuThreadPool
 * @author: muye
 * @date: 2021/7/7
 * @description: cpu型线程池，用于重度计算
 * @history:
 */
public class CpuThreadPool {

    private static final String TAG = "CpuThreadPool";

    /**
     * 参数初始化
     */
    protected static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池最大容纳线程数
     */
    protected static final int MAXIMUM_POOL_SIZE = CPU_COUNT;

    private static final int CORE_POOL_SIZE = 2;

    private static final int TIME_OUT = 60;
    private ThreadPoolExecutor mThreadPoolExecutor;

    private static volatile CpuThreadPool sCpuThreadPool;

    private CpuThreadPool() {
        initThreadPool();
    }

    public static CpuThreadPool getInstance() {
        if (null == sCpuThreadPool) {
            synchronized (CpuThreadPool.class) {
                if (null == sCpuThreadPool) {
                    sCpuThreadPool = new CpuThreadPool();
                }
            }
        }
        return sCpuThreadPool;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return mThreadPoolExecutor;
    }

    @SuppressLint("NewApi")
    private void initThreadPool() {
        final AtomicInteger mAtomicInteger = new AtomicInteger(1);
        SecurityManager var1 = System.getSecurityManager();
        final ThreadGroup group = var1 != null ? var1.getThreadGroup() : Thread.currentThread().getThreadGroup();
        ThreadFactory threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(group, runnable, "track cpu-pool-thread-" + mAtomicInteger.getAndIncrement(), 0);
            }
        };
        mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, Math.max(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE),
                TIME_OUT, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(128),
                threadFactory);
        mThreadPoolExecutor.allowCoreThreadTimeOut(true);
        mThreadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                super.rejectedExecution(r, e);
                Log.i(TAG , "CpuThreadPool rejectedExecution");
            }
        });
    }
}
