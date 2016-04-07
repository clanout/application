package com.clanout.application.module.auth.domain.observer;

import java.util.ArrayList;
import java.util.List;

public class AuthModuleObservers
{
    private List<NewUserRegistrationObserver> newUserRegistrationObservers;

    public AuthModuleObservers()
    {
        newUserRegistrationObservers = new ArrayList<>();
    }

    public void registerNewUserRegistrationObserver(NewUserRegistrationObserver observer)
    {
        newUserRegistrationObservers.add(observer);
    }

    public void onNewUserRegistered(String userId)
    {
        for (NewUserRegistrationObserver observer : newUserRegistrationObservers)
        {
            observer.onNewUserRegistered(userId);
        }
    }
}
