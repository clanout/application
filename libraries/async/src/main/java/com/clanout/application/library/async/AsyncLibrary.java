package com.clanout.application.library.async;

import com.clanout.application.framework.lib.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;

public class AsyncLibrary implements Library
{
    private static Logger LOG = LogManager.getRootLogger();

    public AsyncLibrary(ExecutorService backgroundPool) throws Exception
    {
        LOG.debug("[AsyncLibrary initialized]");
        AsyncPool.init(backgroundPool);
    }

    @Override
    public void destroy()
    {
        AsyncPool.destroy();
        LOG.debug("[AsyncLibrary destroyed]");
    }
}
