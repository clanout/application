package com.clanout.application.library.util;

import com.clanout.application.framework.lib.Library;
import com.clanout.application.library.util.gson.GsonProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UtilLibrary implements Library
{
    private static Logger LOG = LogManager.getRootLogger();

    public UtilLibrary()
    {
        LOG.debug("[UtilLibrary initialized]");
        GsonProvider.init();
    }

    @Override
    public void destroy()
    {
        LOG.debug("[UtilLibrary destroyed]");
    }
}
