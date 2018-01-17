package com.will.tidyspider.core.resources;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by will on 03/01/2018.
 * Singleton
 */
public class ThreadPoolProxy {
    private static ThreadPoolProxy instance;

    private ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolExecutor getThreadPoolExecutor() {
        if(threadPoolExecutor == null)
            initThreadPoolExecutor();
        return threadPoolExecutor;
    }

    public static ThreadPoolProxy getInstance() {
        if(instance == null) {
            synchronized (ThreadPoolProxy.class) {
                if(instance == null) instance = new ThreadPoolProxy();
            }
        }
        return instance;
    }

    private ThreadPoolProxy() {}

    private void initThreadPoolExecutor() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
        this.threadPoolExecutor = new ThreadPoolExecutor(
                getConcurrency(), getConcurrency(), 0L, TimeUnit.MICROSECONDS,
                queue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private int getConcurrency() {
        return RunTimeConfig.getInstance().getServerConfig().getConcurrency();
    }
}
