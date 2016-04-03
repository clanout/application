package com.clanout.application.library.postgres;

import com.clanout.application.framework.lib.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresLibrary implements Library
{
    private static Logger LOG = LogManager.getRootLogger();

    public PostgresLibrary() throws Exception
    {
        LOG.debug("[PostgresLibrary initialized]");
        PostgresDataSource.getInstance().init();
    }

    @Override
    public void destroy()
    {
        PostgresDataSource.getInstance().close();
        LOG.debug("[PostgresLibrary destroyed]");
    }
}
