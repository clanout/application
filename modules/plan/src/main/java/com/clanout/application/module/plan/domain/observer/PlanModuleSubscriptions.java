package com.clanout.application.module.plan.domain.observer;

import com.clanout.application.module.user.context.UserContext;
import com.clanout.application.module.user.domain.observer.LocationUpdateObserver;

public class PlanModuleSubscriptions
{
    private UserContext userContext;

    public PlanModuleSubscriptions(UserContext userContext)
    {
        this.userContext = userContext;

        initUserLocationUpdateSubscription();
    }

    private void initUserLocationUpdateSubscription()
    {
        userContext.registerLocationUpdateObserver(new LocationUpdateObserver()
        {
            @Override
            public void onLocationUpdated(String locationZone, boolean isRelocated)
            {
                System.out.println("Location updated to " + locationZone);
            }
        });
    }
}
