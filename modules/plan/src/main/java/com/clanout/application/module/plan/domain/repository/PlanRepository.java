package com.clanout.application.module.plan.domain.repository;

import com.clanout.application.module.plan.domain.model.Location;
import com.clanout.application.module.plan.domain.model.Plan;

import java.time.OffsetDateTime;

public interface PlanRepository
{
    Plan create(Plan plan);

    void delete(String planId);

    void update(String planId, String description, OffsetDateTime startTime,
                OffsetDateTime endTime, Location location);

    String getAttendeeName(String userId);
}
