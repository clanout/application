package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.CollectionUtils;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.observer.PlanModuleObservers;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import com.clanout.application.module.plan.domain.service.FanoutService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class Invite
{
    private ExecutorService backgroundPool;
    private FeedRepository feedRepository;
    private PlanRepository planRepository;
    private FanoutService fanoutService;
    private PlanModuleObservers observers;

    @Inject
    public Invite(ExecutorService backgroundPool, FeedRepository feedRepository, PlanRepository planRepository,
                  FanoutService fanoutService, PlanModuleObservers observers)
    {
        this.backgroundPool = backgroundPool;
        this.feedRepository = feedRepository;
        this.planRepository = planRepository;
        this.fanoutService = fanoutService;
        this.observers = observers;
    }

    public void execute(Request request) throws InvalidFieldException
    {
        if (StringUtils.isNullOrEmpty(request.planId))
        {
            throw new InvalidFieldException("plan id");
        }

        if (!CollectionUtils.isNullOrEmpty(request.invitee))
        {
            feedRepository.invite(request.userId, request.planId, request.invitee);

            backgroundPool.execute(() -> {
                fanoutService.onInvite(request.userId, request.planId, request.invitee);
            });
        }

        if (!CollectionUtils.isNullOrEmpty(request.mobileInvitee))
        {
            //TODO: Mobile Invitations

            backgroundPool.execute(() -> {

                try
                {
                    Plan plan = planRepository.fetch(request.planId);
                    planRepository.addPhoneInvitations(plan.getId(), request.userId, request.mobileInvitee);
                    observers.onPhoneInvite(plan, request.userId, request.mobileInvitee);
                }
                catch (Exception ignored)
                {
                }
            });
        }
    }

    public static class Request
    {
        public String userId;
        public String planId;
        public List<String> invitee;
        public List<String> mobileInvitee;
    }
}
