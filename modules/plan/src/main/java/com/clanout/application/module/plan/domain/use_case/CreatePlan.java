package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.location.domain.model.LocationZone;
import com.clanout.application.module.location.domain.use_case.GetZone;
import com.clanout.application.module.plan.domain.model.*;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ModuleScope
public class CreatePlan
{
    private GetZone getZone;
    private PlanRepository planRepository;
    private FeedRepository feedRepository;

    @Inject
    public CreatePlan(GetZone getZone, PlanRepository planRepository, FeedRepository feedRepository)
    {
        this.getZone = getZone;
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
    }

    public Response execute(Request request) throws InvalidFieldException
    {
        if (StringUtils.isNullOrEmpty(request.title))
        {
            throw new InvalidFieldException("plan title");
        }

        Type type = null;
        try
        {
            type = Type.valueOf(request.type);
        }
        catch (Exception e)
        {
            throw new InvalidFieldException("plan type");
        }

        if (StringUtils.isNullOrEmpty(request.category))
        {
            throw new InvalidFieldException("plan category");
        }

        if (StringUtils.isNullOrEmpty(request.locationZone) ||
                request.locationZone.equals(LocationZone.UNKNOWN_ZONE.getZoneCode()))
        {
            throw new InvalidFieldException("location zone");
        }

        if (request.startTime == null || request.endTime == null)
        {
            throw new InvalidFieldException("plan timings");
        }

        if (request.endTime.isBefore(request.startTime))
        {
            throw new InvalidFieldException("end time (cannot be before start time)");
        }

        Location location = null;
        String locationZone = LocationZone.UNKNOWN_ZONE.getZoneCode();
        if (request.locationName != null)
        {
            location = new Location();
            location.setName(request.locationName);

            if (request.latitude != null && request.longitude != null)
            {
                location.setLatitude(request.latitude);
                location.setLongitude(request.longitude);

                GetZone.Request getZoneRequest = new GetZone.Request();
                getZoneRequest.latitude = location.getLatitude();
                getZoneRequest.longitude = location.getLongitude();
                locationZone = getZone.execute(getZoneRequest).zoneCode;
            }
        }

        Plan plan = new Plan();
        plan.setTitle(request.title);
        plan.setType(type);
        plan.setCategory(request.category);
        plan.setCreatorId(request.userId);

        Set<String> visibilityZones = new HashSet<>();
        visibilityZones.add(request.locationZone);
        if (!locationZone.equals(LocationZone.UNKNOWN_ZONE.getZoneCode()))
        {
            visibilityZones.add(locationZone);
        }
        plan.setVisibilityZones(new ArrayList<>(visibilityZones));

        plan.setDescription(request.description);
        plan.setStartTime(request.startTime);
        plan.setEndTime(request.endTime);
        plan.setLocation(location);

        plan.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        plan.setUpdatedAt(plan.getCreatedAt());

        Attendee creator = new Attendee();
        creator.setId(request.userId);
        creator.setName(planRepository.getAttendeeName(request.userId));
        creator.setStatus("");
        plan.setAttendees(Arrays.asList(creator));

        plan = planRepository.create(plan);

        plan.setRsvp(Rsvp.YES);
        plan.setStatus("");
        plan.setFriends(new ArrayList<>());
        plan.setInviter(new ArrayList<>());
        plan.setInvitee(new ArrayList<>());

        feedRepository.add(request.userId, plan, false);

        // TODO: Fan Out

        Response response = new Response();
        response.planId = plan.getId();
        return response;
    }

    public static class Request
    {
        public String userId;
        public String title;
        public String type;
        public String category;
        public String locationZone;

        public String description;
        public OffsetDateTime startTime;
        public OffsetDateTime endTime;

        public String locationName;
        public Double latitude;
        public Double longitude;
    }

    public static class Response
    {
        public String planId;
    }
}