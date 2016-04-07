package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.plan.domain.model.Location;
import com.clanout.application.module.plan.domain.model.Plan;

import java.time.OffsetDateTime;

public interface UpdatePlanObserver
{
    void onPlanUpdated(Plan plan, String userId, String description, OffsetDateTime startTime,
                       OffsetDateTime endTime, Location location);
}
