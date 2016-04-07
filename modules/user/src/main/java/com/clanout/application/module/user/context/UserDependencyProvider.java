package com.clanout.application.module.user.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.async.AsyncPool;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.location.domain.use_case.GetZone;
import com.clanout.application.module.user.data.user.PostgresUserRepository;
import com.clanout.application.module.user.domain.observer.UserModuleObservers;
import com.clanout.application.module.user.domain.repository.UserRepository;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutorService;

@Module
class UserDependencyProvider
{
    private UserContext userContext;
    private LocationContext locationContext;

    public UserDependencyProvider(UserContext userContext, LocationContext locationContext)
    {
        this.userContext = userContext;
        this.locationContext = locationContext;
    }

    @Provides
    @ModuleScope
    public ExecutorService provideBackgroundPool()
    {
        return AsyncPool.getInstance().getBackgroundPool();
    }

    @Provides
    @ModuleScope
    public GetZone provideGetZone()
    {
        return locationContext.getZone();
    }

    @Provides
    @ModuleScope
    public UserRepository provideUserRepository()
    {
        return new PostgresUserRepository();
    }

    @Provides
    @ModuleScope
    public UserModuleObservers provideUserModuleObservers()
    {
        return userContext.observers;
    }
}
