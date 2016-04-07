package com.clanout.application.module.chat.domain.observer;

import com.clanout.application.module.auth.context.AuthContext;
import com.clanout.application.module.chat.domain.service.ChatService;
import com.clanout.application.module.plan.context.PlanContext;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.observer.InvitationResponseObserver;

import java.util.concurrent.ExecutorService;

public class ChatModuleSubscriptions
{
    private ExecutorService backgroundPool;
    private AuthContext authContext;
    private PlanContext planContext;
    private ChatService chatService;

    public ChatModuleSubscriptions(ExecutorService backgroundPool, AuthContext authContext, PlanContext planContext, ChatService chatService)
    {
        this.backgroundPool = backgroundPool;
        this.authContext = authContext;
        this.planContext = planContext;
        this.chatService = chatService;
    }

    public void init()
    {
        authContext.registerNewUserRegistrartionObserver(userId -> {
            backgroundPool.execute(() -> {
                chatService.userRegistered(userId);
            });
        });

        planContext.registerCreatePlanObserver((plan, affectedUsers) -> {
            backgroundPool.execute(() -> {
                chatService.planCreated(plan.getId());
            });
        });

        planContext.registerRsvpChangeObserver((userId, plan, rsvp, affectedUsers) -> {
            backgroundPool.execute(() -> {

                if (rsvp == Rsvp.YES)
                {
                    chatService.userJoinedPlan(plan.getId(), userId);
                }
                else if (rsvp == Rsvp.NO)
                {
                    chatService.userLeftPlan(plan.getId(), userId);
                }

            });
        });

        planContext.registerUpdatePlanObserver((planId, userId, description, startTime, endTime, location) -> {
            backgroundPool.execute(() -> {

                if (startTime != null)
                {
                    chatService.planStartTimeUpdated(planId, userId, startTime);
                }

                if (description != null)
                {
                    chatService.planDescriptionUpdated(planId, userId, description);
                }

                if (location != null)
                {
                    chatService.planLocationUpdated(planId, userId, location);
                }

            });
        });

        planContext.registerInvitationResponseObserver((planId, userId, invitationResponse) -> {
            backgroundPool.execute(() -> {
                chatService.planInvitationResponse(planId, userId, invitationResponse);
            });
        });
    }
}
