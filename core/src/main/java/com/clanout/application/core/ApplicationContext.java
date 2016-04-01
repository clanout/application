package com.clanout.application.core;

import com.clanout.application.framework.conf.ConfManager;
import com.clanout.application.framework.lib.Library;
import com.clanout.application.framework.module.Context;
import com.clanout.application.library.util.UtilLibrary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext
{
    private static Logger LOG = LogManager.getRootLogger();

    private Map<Module, Context> modules;
    private Map<Lib, Library> libs;

    public ApplicationContext() throws Exception
    {
        LOG.debug("[ApplicationContext initialized]");

        /* Init Configuration */
        ConfManager.init();

        /* Init Libraries */
        initLibs();

        /* Init Modules */
        initModules();
    }

    public void destroy()
    {
        for (Map.Entry<Module, Context> module : modules.entrySet())
        {
            module.getValue().destroy();
        }

        for (Map.Entry<Lib, Library> library : libs.entrySet())
        {
            library.getValue().destroy();
        }

        LOG.debug("[ApplicationContext destroyed]");
    }

    public Context getContext(Module module)
    {
        return modules.get(module);
    }

    private void initModules() throws Exception
    {
        modules = new HashMap<>();
    }

    private void initLibs() throws Exception
    {
        libs = new HashMap<>();

        UtilLibrary utilLibrary = new UtilLibrary();
        libs.put(Lib.UTIL, utilLibrary);
    }
}
