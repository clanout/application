package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.module.plan.domain.use_case.RecalculateFeed;
import com.clanout.application.module.user.context.UserContext;
import com.clanout.application.module.user.domain.observer.LocationUpdateObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class PlanModuleSubscriptions
{
    private ExecutorService backgroundPool;
    private UserContext userContext;
    private RecalculateFeed recalculateFeed;

    @Inject
    public PlanModuleSubscriptions(ExecutorService backgroundPool, UserContext userContext,
                                   RecalculateFeed recalculateFeed)
    {
        this.backgroundPool = backgroundPool;
        this.userContext = userContext;
        this.recalculateFeed = recalculateFeed;
    }

    public void init()
    {
        userContext.registerLocationUpdateObserver((userId, locationZone, isRelocated) -> {
            backgroundPool.execute(() -> {

                RecalculateFeed.Request request = new RecalculateFeed.Request();
                request.userId = userId;
                try
                {
                    recalculateFeed.execute(request);
                }
                catch (InvalidFieldException ignored)
                {
                }

            });
        });
    }
}
