package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.exception.DeletePlanPermissionException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;

import javax.inject.Inject;

@ModuleScope
public class DeletePlan
{
    private PlanRepository planRepository;
    private FeedRepository feedRepository;

    @Inject
    public DeletePlan(PlanRepository planRepository, FeedRepository feedRepository)
    {
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
    }

    public void execute(Request request)
            throws InvalidFieldException, PlanNotFoundException, DeletePlanPermissionException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan in");
        }

        Plan plan = feedRepository.fetch(request.userId, request.planId);

        if(!plan.getCreatorId().equals(request.userId))
        {
            throw new DeletePlanPermissionException();
        }

        feedRepository.remove(request.userId, request.planId);
        planRepository.delete(request.planId);

        //TODO : (Delete) Fan Out, Notification
    }

    public static class Request
    {
        public String userId;
        public String planId;
    }
}
