package com.clanout.application.module.user.domain.observer;

public interface LocationUpdateObserver
{
    void onLocationUpdated(String locationZone, boolean isRelocated);
}
