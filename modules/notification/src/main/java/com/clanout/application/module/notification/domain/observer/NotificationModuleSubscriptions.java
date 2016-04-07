package com.clanout.application.module.notification.domain.observer;

import com.clanout.application.module.auth.context.AuthContext;
import com.clanout.application.module.notification.domain.service.NotificationService;
import com.clanout.application.module.plan.context.PlanContext;
import com.clanout.application.module.user.context.UserContext;

import java.util.concurrent.ExecutorService;

public class NotificationModuleSubscriptions
{
    private ExecutorService backgroundPool;
    private UserContext userContext;
    private AuthContext authContext;
    private PlanContext planContext;
    private NotificationService notificationService;

    public NotificationModuleSubscriptions(ExecutorService backgroundPool, UserContext userContext, AuthContext authContext,
                                           PlanContext planContext, NotificationService notificationService)
    {
        this.backgroundPool = backgroundPool;
        this.userContext = userContext;
        this.authContext = authContext;
        this.planContext = planContext;
        this.notificationService = notificationService;
    }

    public void init()
    {
        authContext.registerNewUserRegistrartionObserver(userId -> {
            backgroundPool.execute(() -> {
                notificationService.newUser(userId);
            });
        });

        userContext.registerLocationUpdateObserver((userId, locationZone, isRelocated) -> {
            backgroundPool.execute(() -> {

                if (isRelocated)
                {
                    notificationService.userRelocation(userId, locationZone);
                }
            });
        });

        userContext.registerUsersBlockedObserver((userId, blocked) -> {
            backgroundPool.execute(() -> {
                notificationService.blocked(userId, blocked);
            });
        });

        userContext.registerUsersUnblockedObserver((userId, unblocked) -> {
            backgroundPool.execute(() -> {
                notificationService.unblocked(userId, unblocked);
            });
        });

        planContext.registerCreatePlanObserver((plan, affectedUsers) -> {
            backgroundPool.execute(() -> {
                notificationService.planCreated(plan, affectedUsers);
            });
        });

        planContext.registerDeletePlanObserver((plan, affectedUsers) -> {
            backgroundPool.execute(() -> {
                notificationService.planDeleted(plan, affectedUsers);
            });
        });

        planContext.registerUpdatePlanObserver((planId, userId, description, startTime, endTime, location) -> {
            backgroundPool.execute(() -> {
                notificationService.planUpdated(planId, userId, description, startTime, endTime, location);
            });
        });

        planContext.registerRsvpChangeObserver((userId, plan, rsvp, affectedUsers) -> {
            backgroundPool.execute(() -> {
                notificationService.rsvpUpdated(userId, plan, rsvp, affectedUsers);
            });
        });

        planContext.registerInviteObserver((userId, plan, affectedUsers) -> {
            backgroundPool.execute(() -> {
                notificationService.invitation(userId, plan, affectedUsers);
            });
        });

        planContext.registerStatusUpdateObserver((planId, userId, status, isLastMoment) -> {
            if (isLastMoment)
            {
                backgroundPool.execute(() -> {
                    notificationService.statusUpdate(planId, userId, status);
                });
            }
        });

        planContext.registerChatUpdateObserver((planId, message) -> {
            backgroundPool.execute(() -> {
                notificationService.chatUpdate(planId, message);
            });
        });
    }
}
