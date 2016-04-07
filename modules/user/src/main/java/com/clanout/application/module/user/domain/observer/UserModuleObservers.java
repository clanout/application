package com.clanout.application.module.user.domain.observer;

import java.util.ArrayList;
import java.util.List;

public class UserModuleObservers
{
    private List<LocationUpdateObserver> locationUpdateObservers;
    private List<UsersBlockedObserver> usersBlockedObservers;
    private List<UsersUnblockedObserver> usersUnblockedObservers;

    public UserModuleObservers()
    {
        locationUpdateObservers = new ArrayList<>();
        usersBlockedObservers = new ArrayList<>();
        usersUnblockedObservers = new ArrayList<>();
    }

    public void registerLocationUpdateObserver(LocationUpdateObserver observer)
    {
        locationUpdateObservers.add(observer);
    }

    public void registerUsersBlockedObserver(UsersBlockedObserver observer)
    {
        usersBlockedObservers.add(observer);
    }

    public void registerUsersUnblockedObserver(UsersUnblockedObserver observer)
    {
        usersUnblockedObservers.add(observer);
    }

    public void onLocationUpdated(String userId, String locationZone, boolean isRelocated)
    {
        for (LocationUpdateObserver observer : locationUpdateObservers)
        {
            observer.onLocationUpdated(userId, locationZone, isRelocated);
        }
    }

    public void onUsersBlocked(String userId, List<String> blocked)
    {
        for (UsersBlockedObserver observer : usersBlockedObservers)
        {
            observer.onUsersBlocked(userId, blocked);
        }
    }

    public void onUsersUnblocked(String userId, List<String> unblocked)
    {
        for (UsersUnblockedObserver observer : usersUnblockedObservers)
        {
            observer.onUsersUnblocked(userId, unblocked);
        }
    }
}
