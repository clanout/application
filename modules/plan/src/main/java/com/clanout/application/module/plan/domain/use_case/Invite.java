package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.CollectionUtils;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.service.FanoutService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class Invite
{
    private ExecutorService backgroundPool;
    private FeedRepository feedRepository;
    private FanoutService fanoutService;

    @Inject
    public Invite(ExecutorService backgroundPool, FeedRepository feedRepository, FanoutService fanoutService)
    {
        this.backgroundPool = backgroundPool;
        this.feedRepository = feedRepository;
        this.fanoutService = fanoutService;
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

        if(!CollectionUtils.isNullOrEmpty(request.mobileInvitee))
        {
            //TODO: Mobile Invitations
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
