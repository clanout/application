package com.clanout.application.module.location.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.location.data.postgres.PostgresZoneRepository;
import com.clanout.application.module.location.domain.repository.ZoneRepository;
import dagger.Module;
import dagger.Provides;

@Module
class LocationDependencyProvider
{
    @Provides
    @ModuleScope
    public ZoneRepository provideZoneRepository()
    {
        return new PostgresZoneRepository();
    }
}
