package com.clanout.application.module.plan.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.plan.domain.observer.PlanModuleSubscriptions;
import com.clanout.application.module.plan.domain.use_case.*;
import dagger.Component;

@ModuleScope
@Component(modules = PlanDependencyProvider.class)
interface PlanDependencyInjector
{
    PlanModuleSubscriptions subscriptions();

    FetchFeed fetchFeed();

    FetchPlan fetchPlan();

    CreatePlan createPlan();

    UpdatePlan updatePlan();

    DeletePlan deletePlan();

    UpdateRsvp updateRsvp();

    UpdateStatus updateStatus();

    Invite invite();

    ChatUpdate chatUpdate();

    InvitationResponse invitationResponse();

    FetchPendingInvitations fetchPendingInvitations();

    FetchCreatePlanSuggestions fetchCreatePlanSuggestions();
}
