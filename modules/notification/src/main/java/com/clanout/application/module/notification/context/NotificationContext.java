package com.clanout.application.module.notification.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.auth.context.AuthContext;
import com.clanout.application.module.notification.domain.observer.NotificationModuleSubscriptions;
import com.clanout.application.module.notification.domain.use_case.RegisterGcmToken;
import com.clanout.application.module.plan.context.PlanContext;
import com.clanout.application.module.user.context.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class NotificationContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private UserContext userContext;
    private AuthContext authContext;
    private PlanContext planContext;

    private NotificationDependencyInjector injector;

    /* Subscriptions */
    @Inject
    NotificationModuleSubscriptions subscriptions;

    public NotificationContext(UserContext userContext, AuthContext authContext, PlanContext planContext)
    {
        LOG.debug("[NotificationContext initialized]");
        this.userContext = userContext;
        this.authContext = authContext;
        this.planContext = planContext;

        injector = DaggerNotificationDependencyInjector
                .builder()
                .notificationDependencyProvider(new NotificationDependencyProvider(userContext, authContext, planContext))
                .build();

        subscriptions = injector.subscriptions();
        subscriptions.init();
    }

    @Override
    public void destroy()
    {
        injector = null;
        LOG.info("[NotificationContext destroyed]");
    }

    public RegisterGcmToken registerGcmToken()
    {
        return injector.registerGcmToken();
    }
}
