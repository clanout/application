package com.clanout.application.module.user.domain.observer;

import java.util.ArrayList;
import java.util.List;

public class UserModuleObservers
{
    private List<LocationUpdateObserver> locationUpdateObservers;

    public UserModuleObservers()
    {
        locationUpdateObservers = new ArrayList<>();
    }

    public void registerLocationUpdateObserver(LocationUpdateObserver observer)
    {
        locationUpdateObservers.add(observer);
    }

    public void onLocationUpdated(String locationZone, boolean isRelocated)
    {
        for (LocationUpdateObserver observer : locationUpdateObservers)
        {
            observer.onLocationUpdated(locationZone, isRelocated);
        }
    }
}
