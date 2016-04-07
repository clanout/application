package com.clanout.application.module.plan.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.location.context.LocationContext;
import com.clanout.application.module.plan.domain.observer.*;
import com.clanout.application.module.plan.domain.use_case.*;
import com.clanout.application.module.user.context.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class PlanContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private PlanDependencyInjector injector;

    private LocationContext locationContext;
    private UserContext userContext;

    /* Observers */
    PlanModuleObservers observers;

    /* Subscriptions */
    PlanModuleSubscriptions subscriptions;

    public PlanContext(LocationContext locationContext, UserContext userContext)
    {
        LOG.debug("[PlanContext initialized]");
        this.locationContext = locationContext;
        this.userContext = userContext;

        observers = new PlanModuleObservers();

        injector = DaggerPlanDependencyInjector
                .builder()
                .planDependencyProvider(new PlanDependencyProvider(this, locationContext, userContext))
                .build();

        subscriptions = injector.subscriptions();
        subscriptions.init();
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

    public UpdateRsvp updateRsvp()
    {
        return injector.updateRsvp();
    }

    public UpdateStatus updateStatus()
    {
        return injector.updateStatus();
    }

    public Invite invite()
    {
        return injector.invite();
    }

    public ChatUpdate chatUpdate()
    {
        return injector.chatUpdate();
    }

    public InvitationResponse invitationResponse()
    {
        return injector.invitationResponse();
    }

    public FetchPendingInvitations fetchPendingInvitations()
    {
        return injector.fetchPendingInvitations();
    }

    public FetchCreatePlanSuggestions fetchCreatePlanSuggestions()
    {
        return injector.fetchCreatePlanSuggestions();
    }

    public void registerCreatePlanObserver(CreatePlanObserver observer)
    {
        observers.registerCreatePlanObserver(observer);
    }

    public void registerDeletePlanObserver(DeletePlanObserver observer)
    {
        observers.registerDeletePlanObserver(observer);
    }

    public void registerUpdatePlanObserver(UpdatePlanObserver observer)
    {
        observers.registerUpdatePlanObserver(observer);
    }

    public void registerRsvpChangeObserver(RsvpChangeObserver observer)
    {
        observers.registerRsvpChangeObserver(observer);
    }

    public void registerInviteObserver(InviteObserver observer)
    {
        observers.registerInviteObserver(observer);
    }

    public void registerStatusUpdateObserver(StatusUpdateObserver observer)
    {
        observers.registerStatusUpdateObserver(observer);
    }

    public void registerChatUpdateObserver(ChatUpdateObserver observer)
    {
        observers.registerChatUpdateObserver(observer);
    }

    public void registerInvitationResponseObserver(InvitationResponseObserver observer)
    {
        observers.registerInvitationResponseObserver(observer);
    }

    public void registerPhoneInviteObserver(PhoneInviteObserver observer)
    {
        observers.registerPhoneInvitationObserver(observer);
    }
}
