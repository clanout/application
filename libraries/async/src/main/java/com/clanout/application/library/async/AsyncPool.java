package com.clanout.application.library.async;

import java.util.concurrent.ExecutorService;

public class AsyncPool
{
    private static AsyncPool instance;

    public static void init(ExecutorService backgroundPool)
    {
        if (backgroundPool == null)
        {
            throw new IllegalArgumentException();
        }

        instance = new AsyncPool(backgroundPool);
    }

    public static AsyncPool getInstance()
    {
        return instance;
    }

    public static void destroy()
    {
        instance.getBackgroundPool().shutdownNow();
    }

    private ExecutorService backgroundPool;

    private AsyncPool(ExecutorService backgroundPool)
    {
        this.backgroundPool = backgroundPool;
    }

    public ExecutorService getBackgroundPool()
    {
        return backgroundPool;
    }
}
