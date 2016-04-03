package com.clanout.application.module.user.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.location.domain.use_case.GetZone;
import com.clanout.application.module.user.data.postgres.PostgresUserRepository;
import com.clanout.application.module.user.domain.repository.UserRepository;
import dagger.Module;
import dagger.Provides;

@Module
class UserDependencyProvider
{
    private LocationContext locationContext;

    public UserDependencyProvider(LocationContext locationContext)
    {
        this.locationContext = locationContext;
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
}
