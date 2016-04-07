package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.plan.domain.model.Location;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.service.FanoutService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlanModuleObservers
{
    private List<CreatePlanObserver> createPlanObservers;
    private List<DeletePlanObserver> deletePlanObservers;
    private List<UpdatePlanObserver> updatePlanObservers;
    private List<RsvpChangeObserver> rsvpChangeObservers;
    private List<InviteObserver> inviteObservers;
    private List<ChatUpdateObserver> chatUpdateObservers;
    private List<StatusUpdateObserver> statusUpdateObservers;
    private List<InvitationResponseObserver> invitationResponseObservers;
    private List<PhoneInviteObserver> phoneInviteObservers;

    public PlanModuleObservers()
    {
        createPlanObservers = new ArrayList<>();
        deletePlanObservers = new ArrayList<>();
        updatePlanObservers = new ArrayList<>();
        rsvpChangeObservers = new ArrayList<>();
        inviteObservers = new ArrayList<>();
        chatUpdateObservers = new ArrayList<>();
        statusUpdateObservers = new ArrayList<>();
        invitationResponseObservers = new ArrayList<>();
        phoneInviteObservers = new ArrayList<>();
    }

    public void registerCreatePlanObserver(CreatePlanObserver observer)
    {
        createPlanObservers.add(observer);
    }

    public void registerDeletePlanObserver(DeletePlanObserver observer)
    {
        deletePlanObservers.add(observer);
    }

    public void registerUpdatePlanObserver(UpdatePlanObserver observer)
    {
        updatePlanObservers.add(observer);
    }

    public void registerRsvpChangeObserver(RsvpChangeObserver observer)
    {
        rsvpChangeObservers.add(observer);
    }

    public void registerInviteObserver(InviteObserver observer)
    {
        inviteObservers.add(observer);
    }

    public void registerChatUpdateObserver(ChatUpdateObserver observer)
    {
        chatUpdateObservers.add(observer);
    }

    public void registerStatusUpdateObserver(StatusUpdateObserver observer)
    {
        statusUpdateObservers.add(observer);
    }

    public void registerInvitationResponseObserver(InvitationResponseObserver observer)
    {
        invitationResponseObservers.add(observer);
    }

    public void registerPhoneInvitationObserver(PhoneInviteObserver observer)
    {
        phoneInviteObservers.add(observer);
    }

    public void onPlanCreated(Plan plan, List<String> affectedUsers)
    {
        for (CreatePlanObserver observer : createPlanObservers)
        {
            observer.onPlanCreated(plan, affectedUsers);
        }
    }

    public void onPlanDeleted(Plan plan, List<String> affectedUsers)
    {
        for (DeletePlanObserver observer : deletePlanObservers)
        {
            observer.onPlanDeleted(plan, affectedUsers);
        }
    }

    public void onPlanUpdated(Plan plan, String userId, String description, OffsetDateTime startTime,
                              OffsetDateTime endTime, Location location)
    {
        for (UpdatePlanObserver observer : updatePlanObservers)
        {
            observer.onPlanUpdated(plan, userId, description, startTime, endTime, location);
        }
    }

    public void onRsvpUpdated(String userId, Plan plan, Rsvp rsvp,
                              Map<String, FanoutService.FanoutEffect> affectedUsers)
    {
        for (RsvpChangeObserver observer : rsvpChangeObservers)
        {
            observer.onRsvpUpdated(userId, plan, rsvp, affectedUsers);
        }
    }

    public void onInvite(String userId, Plan plan, Map<String, FanoutService.FanoutEffect> affectedUsers)
    {
        for (InviteObserver observer : inviteObservers)
        {
            observer.onInvite(userId, plan, affectedUsers);
        }
    }

    public void onChatUpdate(String planId, String message)
    {
        for (ChatUpdateObserver observer : chatUpdateObservers)
        {
            observer.onChatUpdate(planId, message);
        }
    }

    public void onStatusUpdated(String planId, String userId, String status, boolean isLastMoment)
    {
        for (StatusUpdateObserver observer : statusUpdateObservers)
        {
            observer.onStatusUpdated(planId, userId, status, isLastMoment);
        }
    }

    public void onInvitationResponse(String planId, String userId, String invitationResponse)
    {
        for (InvitationResponseObserver observer : invitationResponseObservers)
        {
            observer.onInvitationResponse(planId, userId, invitationResponse);
        }
    }

    public void onPhoneInvite(Plan plan, String userId, List<String> mobileNumbers)
    {
        for (PhoneInviteObserver observer : phoneInviteObservers)
        {
            observer.onPhoneInvite(plan, userId, mobileNumbers);
        }
    }
}
