package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.service.FanoutService;

import java.util.Map;

public interface RsvpChangeObserver
{
    void onRsvpUpdated(String userId, Plan plan, Rsvp rsvp, Map<String, FanoutService.FanoutEffect> affectedUsers);
}
