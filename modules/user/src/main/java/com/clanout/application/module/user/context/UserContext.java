package com.clanout.application.module.user.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.user.domain.use_case.CreateUser;
import com.clanout.application.module.user.domain.use_case.FetchUserFromUsername;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private UserDependencyInjector injector;

    private LocationContext locationContext;

    public UserContext(LocationContext locationContext)
    {
        LOG.debug("[UserContext initialized]");
        this.locationContext = locationContext;
        injector = DaggerUserDependencyInjector
                .builder()
                .userDependencyProvider(new UserDependencyProvider(locationContext))
                .build();
    }

    @Override
    public void destroy()
    {
        injector = null;
        LOG.info("[UserContext destroyed]");
    }

    public CreateUser createUser()
    {
        return injector.createUser();
    }

    public FetchUserFromUsername fetchUserFromUsername()
    {
        return injector.fetchUserFromUsername();
    }
}
