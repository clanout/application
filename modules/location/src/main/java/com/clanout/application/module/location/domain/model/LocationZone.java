package com.clanout.application.module.location.domain.model;

public class LocationZone
{
    public static final LocationZone UNKNOWN_ZONE = new LocationZone("UNKNOWN_ZONE", 0.0, 0.0, 0.0);

    private final String zoneCode;
    private final double centroidLatitude;
    private final double centroidLongitude;
    private final double range;

    public LocationZone(String zoneCode, double centroidLatitude, double centroidLongitude, double range)
    {
        this.zoneCode = zoneCode;
        this.centroidLatitude = centroidLatitude;
        this.centroidLongitude = centroidLongitude;
        this.range = range;
    }

    public String getZoneCode()
    {
        return zoneCode;
    }

    public double getCentroidLatitude()
    {
        return centroidLatitude;
    }

    public double getCentroidLongitude()
    {
        return centroidLongitude;
    }

    public double getRange()
    {
        return range;
    }

    @Override
    public String toString()
    {
        return "[" + zoneCode + " : (" + centroidLatitude + ", " + centroidLongitude + ") : " + range + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        LocationZone that = (LocationZone) o;

        return zoneCode.equals(that.zoneCode);

    }

    @Override
    public int hashCode()
    {
        return zoneCode.hashCode();
    }
}
