package com.clanout.application.module.location.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.location.domain.model.LocationZone;
import com.clanout.application.module.location.domain.service.LocationService;

import javax.inject.Inject;

@ModuleScope
public class GetZone
{
    LocationService locationService;

    @Inject
    public GetZone(LocationService locationService)
    {
        this.locationService = locationService;
    }

    public Response execute(Request request)
    {
        LocationZone locationZone = locationService.getZone(request.latitude, request.longitude);
        if (locationZone == null)
        {
            locationZone = LocationZone.UNKNOWN_ZONE;
        }

        Response response = new Response();
        response.zoneCode = locationZone.getZoneCode();
        response.zoneName = locationZone.getName();
        return response;
    }

    public static class Request
    {
        public double latitude;
        public double longitude;
    }

    public static class Response
    {
        public String zoneCode;
        public String zoneName;
    }
}
