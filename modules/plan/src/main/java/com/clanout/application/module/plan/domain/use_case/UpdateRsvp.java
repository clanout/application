package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Attendee;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import com.clanout.application.module.plan.domain.service.FanoutService;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class UpdateRsvp
{
    private ExecutorService backgroundPool;
    private PlanRepository planRepository;
    private FeedRepository feedRepository;
    private FanoutService fanoutService;

    @Inject
    public UpdateRsvp(ExecutorService backgroundPool, PlanRepository planRepository,
                      FeedRepository feedRepository, FanoutService fanoutService)
    {
        this.backgroundPool = backgroundPool;
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
        this.fanoutService = fanoutService;
    }

    public void execute(Request request) throws InvalidFieldException, PlanNotFoundException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan id");
        }

        Rsvp newRsvp = null;
        try
        {
            newRsvp = Rsvp.valueOf(request.rsvp);
            if (newRsvp == Rsvp.DEFAULT)
            {
                throw new IllegalArgumentException();
            }
        }
        catch (Exception e)
        {
            throw new InvalidFieldException("rsvp");
        }

        boolean isRsvpUpdated = feedRepository.updateRsvp(request.userId, request.planId, newRsvp);

        if (isRsvpUpdated)
        {
            if (newRsvp == Rsvp.YES)
            {
                Attendee attendee = new Attendee();
                attendee.setId(request.userId);
                attendee.setName(planRepository.getAttendeeName(request.userId));
                attendee.setStatus("");

                planRepository.addAttendee(request.planId, attendee);
            }
            else
            {
                planRepository.deleteAttendee(request.planId, request.userId);
            }

            final Rsvp finalRsvp = newRsvp;
            backgroundPool.execute(() -> {
                fanoutService.onRsvpUpdated(request.userId, request.planId, finalRsvp);
            });
        }
    }

    public static class Request
    {
        public String userId;
        public String planId;
        public String rsvp;
    }
}
