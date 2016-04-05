package com.clanout.application.module.plan.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

public class Plan
{
    private String id;
    private String title;
    private Type type;
    private String category;
    private String creatorId;

    private List<String> visibilityZones;

    private String description;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Location location;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private List<Attendee> attendees;

    /* Contextual Data */
    private Rsvp rsvp;
    private String status;
    private List<String> friends;
    private List<String> inviter;
    private List<String> invitee;

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public Type getType()
    {
        return type;
    }

    public String getCategory()
    {
        return category;
    }

    public String getCreatorId()
    {
        return creatorId;
    }

    public List<String> getVisibilityZones()
    {
        return visibilityZones;
    }

    public String getDescription()
    {
        return description;
    }

    public OffsetDateTime getStartTime()
    {
        return startTime;
    }

    public OffsetDateTime getEndTime()
    {
        return endTime;
    }

    public Location getLocation()
    {
        return location;
    }

    public OffsetDateTime getCreatedAt()
    {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public List<Attendee> getAttendees()
    {
        return attendees;
    }

    public Rsvp getRsvp()
    {
        return rsvp;
    }

    public String getStatus()
    {
        return status;
    }

    public List<String> getFriends()
    {
        return friends;
    }

    public List<String> getInviter()
    {
        return inviter;
    }

    public List<String> getInvitee()
    {
        return invitee;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public void setCreatorId(String creatorId)
    {
        this.creatorId = creatorId;
    }

    public void setVisibilityZones(List<String> visibilityZones)
    {
        this.visibilityZones = visibilityZones;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setStartTime(OffsetDateTime startTime)
    {
        this.startTime = startTime;
    }

    public void setEndTime(OffsetDateTime endTime)
    {
        this.endTime = endTime;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setCreatedAt(OffsetDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public void setAttendees(List<Attendee> attendees)
    {
        this.attendees = attendees;
    }

    public void setRsvp(Rsvp rsvp)
    {
        this.rsvp = rsvp;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setFriends(List<String> friends)
    {
        this.friends = friends;
    }

    public void setInviter(List<String> inviter)
    {
        this.inviter = inviter;
    }

    public void setInvitee(List<String> invitee)
    {
        this.invitee = invitee;
    }
}
