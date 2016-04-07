package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.observer.PlanModuleObservers;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class ChatUpdate
{
    private ExecutorService backgroundPool;
    private PlanModuleObservers observers;

    @Inject
    public ChatUpdate(ExecutorService backgroundPool, PlanModuleObservers observers)
    {
        this.backgroundPool = backgroundPool;
        this.observers = observers;
    }

    public void execute(Request request)
    {
        if (StringUtils.isNullOrEmpty(request.planId) || StringUtils.isNullOrEmpty(request.message))
        {
            return;
        }

        backgroundPool.execute(() -> {
            observers.onChatUpdate(request.planId, request.message);
        });
    }

    public static class Request
    {
        public String planId;
        public String message;
    }
}
