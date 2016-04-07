package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.plan.domain.model.PlanSuggestion;
import com.clanout.application.module.plan.domain.repository.PlanRepository;

import javax.inject.Inject;
import java.util.List;

@ModuleScope
public class FetchCreatePlanSuggestions
{
    private PlanRepository planRepository;

    @Inject
    public FetchCreatePlanSuggestions(PlanRepository planRepository)
    {
        this.planRepository = planRepository;
    }

    public Response execute()
    {
        List<PlanSuggestion> planSuggestions = planRepository.fetchCreateSuggestions();

        Response response = new Response();
        response.createSuggestions = planSuggestions;
        return response;
    }

    public static class Response
    {
        public List<PlanSuggestion> createSuggestions;
    }
}
