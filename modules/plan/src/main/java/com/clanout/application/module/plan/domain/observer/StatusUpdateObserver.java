package com.clanout.application.module.plan.domain.observer;

public interface StatusUpdateObserver
{
    void onStatusUpdated(String planId, String userId, String status, boolean isLastMoment);
}
