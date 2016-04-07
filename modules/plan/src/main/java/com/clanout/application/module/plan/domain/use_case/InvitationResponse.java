package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.observer.PlanModuleObservers;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class InvitationResponse
{
    private ExecutorService backgroundPool;
    private PlanModuleObservers observers;

    @Inject
    public InvitationResponse(ExecutorService backgroundPool, PlanModuleObservers observers)
    {
        this.backgroundPool = backgroundPool;
        this.observers = observers;
    }

    public void execute(Request request) throws InvalidFieldException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan id");
        }

        if (StringUtils.isNullOrEmpty(request.invitationResponse))
        {
            throw new InvalidFieldException("invitation response");
        }

        backgroundPool.execute(() -> {
            observers.onInvitationResponse(request.planId, request.userId, request.invitationResponse);
        });
    }

    public static class Request
    {
        public String userId;
        public String planId;
        public String invitationResponse;
    }
}
