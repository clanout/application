package com.clanout.application.library.redis;

import com.clanout.application.framework.lib.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedisLibrary implements Library
{
    private static Logger LOG = LogManager.getRootLogger();

    public RedisLibrary() throws Exception
    {
        LOG.debug("[RedisLibrary initialized]");
        RedisDataSource.getInstance().init();
    }

    @Override
    public void destroy()
    {
        RedisDataSource.getInstance().close();
        LOG.debug("[RedisLibrary destroyed]");
    }
}
