package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.plan.domain.model.Location;

import java.time.OffsetDateTime;

public interface UpdatePlanObserver
{
    void onPlanUpdated(String planId, String userId, String description, OffsetDateTime startTime,
                       OffsetDateTime endTime, Location location);
}
