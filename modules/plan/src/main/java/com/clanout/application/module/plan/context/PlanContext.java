package com.clanout.application.module.plan.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.plan.domain.observer.PlanModuleSubscriptions;
import com.clanout.application.module.plan.domain.use_case.*;
import com.clanout.application.module.user.context.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlanContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private PlanDependencyInjector injector;

    private LocationContext locationContext;
    private UserContext userContext;

    private PlanModuleSubscriptions subscriptions;

    public PlanContext(LocationContext locationContext, UserContext userContext)
    {
        LOG.debug("[PlanContext initialized]");
        this.locationContext = locationContext;
        this.userContext = userContext;

        injector = DaggerPlanDependencyInjector
                .builder()
                .planDependencyProvider(new PlanDependencyProvider(locationContext, userContext))
                .build();

        subscriptions = new PlanModuleSubscriptions(userContext);
    }

    @Override
    public void destroy()
    {
        injector = null;
        LOG.info("[PlanContext destroyed]");
    }

    public FetchFeed fetchFeed()
    {
        return injector.fetchFeed();
    }

    public FetchPlan fetchPlan()
    {
        return injector.fetchPlan();
    }

    public CreatePlan createPlan()
    {
        return injector.createPlan();
    }

    public UpdatePlan updatePlan()
    {
        return injector.updatePlan();
    }

    public DeletePlan deletePlan()
    {
        return injector.deletePlan();
    }
}
