package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.user.context.UserContext;
import com.clanout.application.module.user.domain.observer.LocationUpdateObserver;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class PlanModuleSubscriptions
{
    private ExecutorService backgroundPool;
    private UserContext userContext;

    @Inject
    public PlanModuleSubscriptions(ExecutorService backgroundPool, UserContext userContext)
    {
        this.backgroundPool = backgroundPool;
        this.userContext = userContext;
    }

    public void init()
    {
        userContext.registerLocationUpdateObserver((userId, locationZone, isRelocated) -> {
            backgroundPool.execute(() -> {
                System.out.println(userId + " relocated to " + locationZone);
            });
        });
    }
}
