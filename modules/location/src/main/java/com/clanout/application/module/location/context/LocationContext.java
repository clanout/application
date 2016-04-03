package com.clanout.application.module.location.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.location.domain.use_case.GetZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private LocationDependencyInjector injector;

    public LocationContext()
    {
        LOG.debug("[LocationContext initialized]");
        injector = DaggerLocationDependencyInjector.create();
    }

    @Override
    public void destroy()
    {
        injector = null;
        LOG.info("[LocationContext destroyed]");
    }

    public GetZone getZone()
    {
        return injector.getZone();
    }
}
