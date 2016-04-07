package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.plan.domain.model.Plan;

import java.util.List;

public interface PhoneInviteObserver
{
    void onPhoneInvite(Plan plan, String userId, List<String> mobileNumbers);
}
