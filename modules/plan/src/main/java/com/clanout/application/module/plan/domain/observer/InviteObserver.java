package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.service.FanoutService;

import java.util.Map;

public interface InviteObserver
{
    void onInvite(String userId, Plan plan, Map<String, FanoutService.FanoutEffect> affectedUsers);
}
