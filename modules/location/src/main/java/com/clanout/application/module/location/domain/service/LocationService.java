package com.clanout.application.module.location.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.CollectionUtils;
import com.clanout.application.module.location.domain.model.LocationZone;
import com.clanout.application.module.location.domain.repository.ZoneRepository;

import javax.inject.Inject;
import java.util.List;

@ModuleScope
public class LocationService
{
    private static final double RADIUS_EARTH = 6371000.00;
    private static List<LocationZone> LOCATION_ZONES;

    private ZoneRepository zoneRepository;

    @Inject
    public LocationService(ZoneRepository zoneRepository)
    {
        if (CollectionUtils.isNullOrEmpty(LOCATION_ZONES))
        {
            LOCATION_ZONES = zoneRepository.fetchAllZones();
            LOCATION_ZONES.remove(LocationZone.UNKNOWN_ZONE);
        }
    }

    public LocationZone getZone(double latitude, double longitude)
    {
        for (LocationZone locationZone : LOCATION_ZONES)
        {
            double centroidLatitude = locationZone.getCentroidLatitude();
            double centroidLongitude = locationZone.getCentroidLongitude();
            double range = locationZone.getRange();
            if (calculateDistance(centroidLatitude, centroidLongitude, latitude, longitude) < range)
            {
                return locationZone;
            }
        }

        return null;
    }

    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIUS_EARTH * c;
    }
}
