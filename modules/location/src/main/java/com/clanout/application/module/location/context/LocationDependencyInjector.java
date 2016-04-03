package com.clanout.application.module.location.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.location.domain.repository.ZoneRepository;
import com.clanout.application.module.location.domain.use_case.GetZone;
import dagger.Component;

@ModuleScope
@Component(modules = LocationDependencyProvider.class)
interface LocationDependencyInjector
{
    GetZone getZone();
}
