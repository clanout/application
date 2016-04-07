package com.clanout.application.module.chat.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.auth.context.AuthContext;
import com.clanout.application.module.chat.domain.observer.ChatModuleSubscriptions;
import com.clanout.application.module.plan.context.PlanContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class ChatContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private AuthContext authContext;
    private PlanContext planContext;

    private NotificationDependencyInjector injector;

    /* Subscriptions */
    @Inject
    ChatModuleSubscriptions subscriptions;

    public ChatContext(AuthContext authContext, PlanContext planContext)
    {
        LOG.debug("[ChatContext initialized]");
        this.authContext = authContext;
        this.planContext = planContext;

        injector = DaggerNotificationDependencyInjector
                .builder()
                .chatDependencyProvider(new ChatDependencyProvider(authContext, planContext))
                .build();

        subscriptions = injector.chatModuleSubscriptions();
        subscriptions.init();
    }

    @Override
    public void destroy()
    {
        injector = null;
        LOG.info("[ChatContext destroyed]");
    }
}
