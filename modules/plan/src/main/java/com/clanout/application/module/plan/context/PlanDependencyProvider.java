package com.clanout.application.module.plan.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.location.domain.use_case.GetZone;
import com.clanout.application.module.plan.data.plan.FeedRepositoryImpl;
import com.clanout.application.module.plan.data.plan.PlanRepositoryImpl;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import com.clanout.application.module.user.context.UserContext;
import dagger.Module;
import dagger.Provides;

@Module
class PlanDependencyProvider
{
    private LocationContext locationContext;
    private UserContext userContext;

    public PlanDependencyProvider(LocationContext locationContext, UserContext userContext)
    {
        this.locationContext = locationContext;
        this.userContext = userContext;
    }

    @Provides
    @ModuleScope
    public PlanRepository providePlanRepository()
    {
        return new PlanRepositoryImpl();
    }

    @Provides
    @ModuleScope
    public FeedRepository provideFeedRepository()
    {
        return new FeedRepositoryImpl();
    }

    @Provides
    @ModuleScope
    public GetZone provideGetZone()
    {
        return locationContext.getZone();
    }
}
