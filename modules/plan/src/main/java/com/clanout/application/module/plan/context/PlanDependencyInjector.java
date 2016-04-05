package com.clanout.application.module.plan.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.plan.domain.use_case.CreatePlan;
import com.clanout.application.module.plan.domain.use_case.FetchFeed;
import com.clanout.application.module.plan.domain.use_case.FetchPlan;
import dagger.Component;

@ModuleScope
@Component(modules = PlanDependencyProvider.class)
interface PlanDependencyInjector
{
    FetchFeed fetchFeed();

    FetchPlan fetchPlan();

    CreatePlan createPlan();
}
