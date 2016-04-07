package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.plan.domain.model.Plan;

import java.util.List;

public interface DeletePlanObserver
{
    void onPlanDeleted(Plan plan, List<String> affectedUsers);
}
