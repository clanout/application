package com.clanout.application.module.plan.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.async.AsyncPool;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.location.domain.use_case.GetZone;
import com.clanout.application.module.plan.data.plan.FeedRepositoryImpl;
import com.clanout.application.module.plan.data.plan.PlanRepositoryImpl;
import com.clanout.application.module.plan.domain.observer.PlanModuleObservers;
import com.clanout.application.module.plan.domain.observer.PlanModuleSubscriptions;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import com.clanout.application.module.user.context.UserContext;
import com.clanout.application.module.user.domain.use_case.FetchFriends;
import com.clanout.application.module.user.domain.use_case.UpdateMobile;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutorService;

@Module
class PlanDependencyProvider
{
    private PlanContext planContext;
    private LocationContext locationContext;
    private UserContext userContext;

    public PlanDependencyProvider(PlanContext planContext, LocationContext locationContext, UserContext userContext)
    {
        this.planContext = planContext;
        this.locationContext = locationContext;
        this.userContext = userContext;
    }

    @Provides
    @ModuleScope
    public ExecutorService provideBackgroundPool()
    {
        return AsyncPool.getInstance().getBackgroundPool();
    }

    @Provides
    @ModuleScope
    public PlanModuleObservers providePlanModuleObservers()
    {
        return planContext.observers;
    }

    @Provides
    @ModuleScope
    public PlanModuleSubscriptions providePlanModuleSubscriptions(ExecutorService backgroundPool)
    {
        return new PlanModuleSubscriptions(backgroundPool, userContext);
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

    @Provides
    @ModuleScope
    public FetchFriends provideFetchFriends()
    {
        return userContext.fetchFriends();
    }

    @Provides
    @ModuleScope
    public UpdateMobile provideUpdateMobile()
    {
        return userContext.updateMobile();
    }
}
