package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.plan.domain.model.Attendee;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@ModuleScope
public class ArchivePlans
{
    private static Logger LOG = LogManager.getRootLogger();

    private PlanRepository planRepository;
    private FeedRepository feedRepository;

    @Inject
    public ArchivePlans(PlanRepository planRepository, FeedRepository feedRepository)
    {
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
    }

    public void execute()
    {
        OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC).minusDays(2);
        LOG.info("[ARCHIVE] Starting archiving for plans with end_time before " + timestamp.toString());

        List<Plan> plans = planRepository.fetchExpiredPlans(timestamp);
        for (Plan plan : plans)
        {
            List<String> viewerIds = feedRepository.fetchPlanViewers(plan.getId());
            List<String> attendeeIds = plan.getAttendees()
                                           .stream()
                                           .map(Attendee::getId)
                                           .collect(Collectors.toList());

            LOG.info("[ARCHIVE] plan_id=" + plan.getId() + "; attendees=" + attendeeIds + "; viewers=" + viewerIds);

            for (String viewer : viewerIds)
            {
                feedRepository.remove(viewer, plan.getId());
            }

            planRepository.delete(plan.getId());
            planRepository.archive(plan);
            feedRepository.archive(plan.getId(), attendeeIds);
        }

        LOG.info("[ARCHIVE] Archiving complete for " + plans.size() + " plans");
    }
}
