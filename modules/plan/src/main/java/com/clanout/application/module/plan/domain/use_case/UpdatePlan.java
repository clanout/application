package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.exception.UpdatePlanPermissionException;
import com.clanout.application.module.plan.domain.model.Attendee;
import com.clanout.application.module.plan.domain.model.Location;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;

@ModuleScope
public class UpdatePlan
{
    private PlanRepository planRepository;
    private FeedRepository feedRepository;

    @Inject
    public UpdatePlan(PlanRepository planRepository, FeedRepository feedRepository)
    {
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
    }

    public void execute(Request request) throws PlanNotFoundException,
            InvalidFieldException, UpdatePlanPermissionException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan in");
        }

        Plan plan = feedRepository.fetch(request.userId, request.planId);

        boolean isEditable = false;
        List<Attendee> attendees = plan.getAttendees();
        for (Attendee attendee : attendees)
        {
            if (attendee.getId().equals(request.userId))
            {
                isEditable = true;
            }
        }

        if (!isEditable)
        {
            throw new UpdatePlanPermissionException();
        }

        if (request.endTime != null)
        {
            if (request.startTime != null)
            {
                if (request.endTime.isBefore(request.endTime))
                {
                    throw new InvalidFieldException("end time (cannot be before start time)");
                }
            }
            else
            {
                if (request.endTime.isBefore(plan.getStartTime()))
                {
                    throw new InvalidFieldException("end time (cannot be before start time)");
                }
            }
        }

        Location location = null;
        if (request.locationName != null)
        {
            location = new Location();
            location.setName(request.locationName);

            if (request.latitude != null && request.longitude != null)
            {
                location.setLatitude(request.latitude);
                location.setLongitude(request.longitude);
            }
        }

        planRepository.update(request.planId, request.description,
                              request.startTime, request.endTime, location);

        //TODO : (Update) Fan Out, Notification
    }

    public static class Request
    {
        public String userId;
        public String planId;
        public String description;
        public OffsetDateTime startTime;
        public OffsetDateTime endTime;
        public String locationName;
        public Double latitude;
        public Double longitude;
    }
}
