package com.clanout.application.library.mongo;

import com.clanout.application.framework.lib.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MongoLibrary implements Library
{
    private static Logger LOG = LogManager.getRootLogger();

    public MongoLibrary() throws Exception
    {
        LOG.debug("[MongoLibrary initialized]");
        MongoDataSource.getInstance().init();
    }

    @Override
    public void destroy()
    {
        MongoDataSource.getInstance().close();
        LOG.debug("[MongoLibrary destroyed]");
    }
}
