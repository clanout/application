package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.module.location.domain.use_case.GetZone;
import com.clanout.application.module.user.domain.observer.UserModuleObservers;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

public class UpdateLocation
{
    private ExecutorService backgroundPool;
    private GetZone getZone;
    private UserRepository userRepository;

    private UserModuleObservers observers;

    @Inject
    public UpdateLocation(ExecutorService backgroundPool, GetZone getZone,
                          UserRepository userRepository, UserModuleObservers observers)
    {
        this.backgroundPool = backgroundPool;
        this.getZone = getZone;
        this.userRepository = userRepository;
        this.observers = observers;
    }

    public Response execute(Request request)
    {
        GetZone.Request getZoneRequest = new GetZone.Request();
        getZoneRequest.latitude = request.latitude;
        getZoneRequest.longitude = request.longitude;
        GetZone.Response getZoneResponse = getZone.execute(getZoneRequest);

        String locationZone = getZoneResponse.zoneCode;
        boolean isRelocated = userRepository.updateLocation(request.userId, locationZone);

        backgroundPool.execute(() -> {
            observers.onLocationUpdated(request.userId, locationZone, isRelocated);
        });

        Response response = new Response();
        response.isRelocated = isRelocated;
        response.locationZone = locationZone;
        response.locationName = getZoneResponse.zoneName;
        return response;
    }

    public static class Request
    {
        public String userId;
        public double latitude;
        public double longitude;
    }

    public static class Response
    {
        public String locationZone;
        public String locationName;
        public boolean isRelocated;
    }
}
