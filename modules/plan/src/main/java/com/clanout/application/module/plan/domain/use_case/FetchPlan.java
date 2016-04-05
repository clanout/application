package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.repository.FeedRepository;

import javax.inject.Inject;

@ModuleScope
public class FetchPlan
{
    private FeedRepository feedRepository;

    @Inject
    public FetchPlan(FeedRepository feedRepository)
    {
        this.feedRepository = feedRepository;
    }

    public Response execute(Request request) throws InvalidFieldException, PlanNotFoundException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan id");
        }

        Plan plan = feedRepository.fetch(request.userId, request.planId);

        Response response = new Response();
        response.plan = plan;
        return response;
    }

    public static class Request
    {
        public String userId;
        public String planId;
    }

    public static class Response
    {
        public Plan plan;
    }
}
