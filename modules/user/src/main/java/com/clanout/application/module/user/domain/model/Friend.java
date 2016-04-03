package com.clanout.application.module.user.domain.model;

public class Friend
{
    private String userId;
    private String name;
    private String locationZone;
    private boolean isBlocked;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLocationZone()
    {
        return locationZone;
    }

    public void setLocationZone(String locationZone)
    {
        this.locationZone = locationZone;
    }

    public boolean isBlocked()
    {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked)
    {
        this.isBlocked = isBlocked;
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

        Friend friend = (Friend) o;

        return userId.equals(friend.userId);

    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }
}
