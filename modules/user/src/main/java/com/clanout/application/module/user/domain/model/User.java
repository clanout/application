package com.clanout.application.module.user.domain.model;

import java.time.OffsetDateTime;

public class User
{
    private String userId;
    private String firstname;
    private String lastname;
    private String email;
    private String mobileNumber;
    private String gender;
    private String username;
    private String usernameType;
    private String locationZone;
    private OffsetDateTime createdAt;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsernameType()
    {
        return usernameType;
    }

    public void setUsernameType(String usernameType)
    {
        this.usernameType = usernameType;
    }

    public String getLocationZone()
    {
        return locationZone;
    }

    public void setLocationZone(String locationZone)
    {
        this.locationZone = locationZone;
    }

    public OffsetDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt)
    {
        this.createdAt = createdAt;
    }
}
