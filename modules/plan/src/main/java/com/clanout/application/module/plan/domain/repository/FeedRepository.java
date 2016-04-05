package com.clanout.application.module.plan.domain.repository;

import com.clanout.application.module.plan.domain.exception.FeedNotFoundException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Feed;
import com.clanout.application.module.plan.domain.model.Plan;

import java.time.OffsetDateTime;

public interface FeedRepository
{
    void add(String userId, Plan plan, boolean markFeedUpdated);

    void remove(String userId, String planId);

    Plan fetch(String userId, String planId)throws PlanNotFoundException;

    Feed fetch(String userId, OffsetDateTime lastUpdated) throws FeedNotFoundException;
}
