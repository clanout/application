package com.clanout.application.module.user.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.user.domain.observer.LocationUpdateObserver;
import com.clanout.application.module.user.domain.observer.UserModuleObservers;
import com.clanout.application.module.user.domain.observer.UsersBlockedObserver;
import com.clanout.application.module.user.domain.observer.UsersUnblockedObserver;
import com.clanout.application.module.user.domain.use_case.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private UserDependencyInjector injector;

    private LocationContext locationContext;

    /* Observers */
    UserModuleObservers observers;

    public UserContext(LocationContext locationContext)
    {
        LOG.debug("[UserContext initialized]");
        this.locationContext = locationContext;
        injector = DaggerUserDependencyInjector
                .builder()
                .userDependencyProvider(new UserDependencyProvider(this, locationContext))
                .build();

        observers = new UserModuleObservers();
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

    public FetchUser fetchUser()
    {
        return injector.fetchUser();
    }

    public FetchUserFromUsername fetchUserFromUsername()
    {
        return injector.fetchUserFromUsername();
    }

    public UpdateLocation updateLocation()
    {
        return injector.updateLocation();
    }

    public UpdateMobile updateMobile()
    {
        return injector.updateMobile();
    }

    public AddFriends addFriends()
    {
        return injector.addFriends();
    }

    public FetchFriends fetchFriends()
    {
        return injector.fetchFriends();
    }

    public BlockFriends blockFriends()
    {
        return injector.blockFriends();
    }

    public FetchRegisteredContacts fetchRegisteredContacts()
    {
        return injector.fetchRegisteredContacts();
    }

    public void registerLocationUpdateObserver(LocationUpdateObserver observer)
    {
        observers.registerLocationUpdateObserver(observer);
    }

    public void registerUsersBlockedObserver(UsersBlockedObserver observer)
    {
        observers.registerUsersBlockedObserver(observer);
    }

    public void registerUsersUnblockedObserver(UsersUnblockedObserver observer)
    {
        observers.registerUsersUnblockedObserver(observer);
    }
}
