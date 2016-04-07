package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.use_case.FetchFriends;
import com.clanout.application.module.user.domain.use_case.UpdateMobile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ModuleScope
public class FetchPendingInvitations
{
    private FetchFriends fetchFriends;
    private UpdateMobile updateMobile;
    private PlanRepository planRepository;
    private FeedRepository feedRepository;

    @Inject
    public FetchPendingInvitations(FetchFriends fetchFriends, UpdateMobile updateMobile,
                                   PlanRepository planRepository, FeedRepository feedRepository)
    {
        this.fetchFriends = fetchFriends;
        this.updateMobile = updateMobile;
        this.planRepository = planRepository;
        this.feedRepository = feedRepository;
    }

    public Response execute(Request request) throws InvalidFieldException
    {
        UpdateMobile.Request updateMobileRequest = new UpdateMobile.Request();
        updateMobileRequest.userId = request.userId;
        updateMobileRequest.mobileNumber = request.mobileNumber;
        updateMobile.execute(updateMobileRequest);

        FetchFriends.Request fetchFriendsRequest = new FetchFriends.Request();
        fetchFriendsRequest.userId = request.userId;
        FetchFriends.Response fetchFriendsResponse = fetchFriends.execute(fetchFriendsRequest);
        List<String> friendIds = fetchFriendsResponse
                .friends
                .stream()
                .map(Friend::getUserId)
                .collect(Collectors.toList());

        Map<String, Set<String>> pendingInvitations = planRepository.fetchPendingInvitations(request.mobileNumber);
        planRepository.deletePendingInvitations(request.mobileNumber);

        int expiredCount = 0;
        List<Plan> activePlans = new ArrayList<>();
        for (Map.Entry<String, Set<String>> pendingInvitation : pendingInvitations.entrySet())
        {
            Plan plan = null;
            try
            {
                plan = feedRepository.fetch(pendingInvitation.getKey(), request.userId);

                // Plan already visible to the user
                for (String inviter : pendingInvitation.getValue())
                {
                    plan.getInviter().add(inviter);
                    if (friendIds.contains(inviter))
                    {
                        plan.getFriends().add(inviter);
                    }
                }
            }
            catch (PlanNotFoundException e)
            {
                try
                {
                    plan = planRepository.fetch(pendingInvitation.getKey());

                    // Plan to be added to feed
                    plan.setRsvp(Rsvp.DEFAULT);
                    plan.setStatus("");
                    plan.setFriends(new ArrayList<>());
                    plan.setInviter(new ArrayList<>());
                    plan.setInvitee(new ArrayList<>());

                    for (String inviter : pendingInvitation.getValue())
                    {
                        plan.getInviter().add(inviter);
                        if (friendIds.contains(inviter))
                        {
                            plan.getFriends().add(inviter);
                        }
                    }
                }
                catch (PlanNotFoundException e1)
                {
                    expiredCount++;
                }
            }

            if (plan != null)
            {
                activePlans.add(plan);
                feedRepository.add(request.userId, plan);
            }
        }

        Response response = new Response();
        response.activePlans = activePlans;
        response.expiredPlanCount = expiredCount;
        return response;
    }

    public static class Request
    {
        public String userId;
        public String mobileNumber;
    }

    public static class Response
    {
        public int expiredPlanCount;
        public List<Plan> activePlans;
    }
}
