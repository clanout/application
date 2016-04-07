package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.observer.PlanModuleObservers;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class UpdateStatus
{
    private ExecutorService backgroundPool;
    private PlanRepository planRepository;
    private FeedRepository feedRepository;
    private PlanModuleObservers observers;

    @Inject
    public UpdateStatus(ExecutorService backgroundPool, PlanRepository planRepository,
                        FeedRepository feedRepository, PlanModuleObservers observers)
    {
        this.backgroundPool = backgroundPool;
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
        this.observers = observers;
    }

    public void execute(Request request) throws InvalidFieldException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan id");
        }

        if (request.status == null)
        {
            throw new InvalidFieldException("status");
        }

        planRepository.updateStatus(request.planId, request.userId, request.status);
        feedRepository.updateStatus(request.userId, request.planId, request.status);

        backgroundPool.execute(() -> {
            observers.onStatusUpdated(request.planId, request.userId, request.status, request.isLastMoment);
        });
    }

    public static class Request
    {
        public String userId;
        public String planId;
        public String status;
        public boolean isLastMoment;
    }
}
