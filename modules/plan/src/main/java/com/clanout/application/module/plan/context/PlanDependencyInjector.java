package com.clanout.application.module.plan.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.plan.domain.use_case.*;
import dagger.Component;

@ModuleScope
@Component(modules = PlanDependencyProvider.class)
interface PlanDependencyInjector
{
    FetchFeed fetchFeed();

    FetchPlan fetchPlan();

    CreatePlan createPlan();

    UpdatePlan updatePlan();

    DeletePlan deletePlan();

    UpdateRsvp updateRsvp();
}
