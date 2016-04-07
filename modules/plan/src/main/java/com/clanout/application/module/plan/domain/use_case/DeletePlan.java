package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.exception.DeletePlanPermissionException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import com.clanout.application.module.plan.domain.service.FanoutService;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class DeletePlan
{
    private ExecutorService backgroundPool;
    private PlanRepository planRepository;
    private FeedRepository feedRepository;
    private FanoutService fanoutService;

    @Inject
    public DeletePlan(ExecutorService backgroundPool, PlanRepository planRepository,
                      FeedRepository feedRepository, FanoutService fanoutService)
    {
        this.backgroundPool = backgroundPool;
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
        this.fanoutService = fanoutService;
    }

    public void execute(Request request)
            throws InvalidFieldException, PlanNotFoundException, DeletePlanPermissionException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan id");
        }

        Plan plan = planRepository.fetch(request.planId);

        if (!plan.getCreatorId().equals(request.userId))
        {
            throw new DeletePlanPermissionException();
        }

        feedRepository.remove(request.userId, request.planId);

        /* Fan Out */
        backgroundPool.execute(() -> {
            fanoutService.onDelete(request.userId, plan);
            planRepository.delete(request.planId);
        });
    }

    public static class Request
    {
        public String userId;
        public String planId;
    }
}
