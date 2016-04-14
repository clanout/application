package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.module.plan.domain.exception.FeedNotFoundException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Feed;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.model.Type;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.use_case.FetchFriends;
import com.clanout.application.module.user.domain.use_case.FetchUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ModuleScope
public class RecalculateFeed
{
    private static Logger LOG = LogManager.getRootLogger();

    private FetchUser fetchUser;
    private FetchFriends fetchFriends;
    private FeedRepository feedRepository;

    @Inject
    public RecalculateFeed(FetchUser fetchUser, FetchFriends fetchFriends, FeedRepository feedRepository)
    {
        this.fetchUser = fetchUser;
        this.fetchFriends = fetchFriends;
        this.feedRepository = feedRepository;
    }

    public Response execute(Request request) throws InvalidFieldException
    {
        LOG.info("[Feed Recalculation] Started for user_id = " + request.userId);

        String userId = request.userId;

        FetchUser.Request fetchUserRequest = new FetchUser.Request();
        fetchUserRequest.userId = request.userId;
        User user = fetchUser.execute(fetchUserRequest).user;

        FetchFriends.Request fetchFriendsRequest = new FetchFriends.Request();
        fetchFriendsRequest.userId = request.userId;
        fetchFriendsRequest.locationZone = user.getLocationZone();
        List<String> friends = fetchFriends.execute(fetchFriendsRequest)
                .friends
                .stream()
                .map(Friend::getUserId)
                .collect(Collectors.toList());


        try
        {
            Feed feed = feedRepository.fetchFeed(userId, null);

            /* Remove non relevant plans */
            List<Plan> oldPlans = feed.getPlans();
            for (Plan plan : oldPlans)
            {
                if ((plan.getRsvp() != Rsvp.YES) &&
                        !(plan.getCreatorId().equals(userId)) &&
                        (plan.getInviter().isEmpty()))
                {
                    feedRepository.remove(userId, plan.getId());
                }
            }
        }
        catch (FeedNotFoundException e)
        {
        }

        /* Recalculate feed from friend's events */
        for (String friend : friends)
        {
            try
            {
                Feed friendFeed = feedRepository.fetchFeed(friend, null);
                List<Plan> friendPlans = friendFeed.getPlans();
                for (Plan plan : friendPlans)
                {
                    if (plan.getType() == Type.OPEN)
                    {
                        try
                        {
                            Plan userPlan = feedRepository.fetch(userId, plan.getId());

                            // Plan already in feed
                            if (!userPlan.getFriends().contains(friend))
                            {
                                userPlan.getFriends().add(friend);
                            }
                            feedRepository.add(userId, userPlan);
                        }
                        catch (PlanNotFoundException e)
                        {
                            plan.setRsvp(Rsvp.DEFAULT);
                            plan.setStatus("");
                            plan.setFriends(Arrays.asList(friend));

                            if (plan.getInvitee().contains(userId))
                            {
                                plan.setInviter(Arrays.asList(friend));
                            }
                            else
                            {
                                plan.setInviter(new ArrayList<>());
                            }

                            plan.setInvitee(new ArrayList<>());

                            feedRepository.add(userId, plan);
                        }
                    }
                }
            }
            catch (Exception ignored)
            {
            }
        }

        Feed feed = null;
        try
        {
            feed = feedRepository.fetchFeed(request.userId, null);
        }
        catch (FeedNotFoundException e)
        {
            /* Create fresh feed */
            feedRepository.initializeFeed(request.userId);

            try
            {
                feed = feedRepository.fetchFeed(request.userId, null);
            }
            catch (FeedNotFoundException ignored)
            {
                // not possible
            }
        }

        LOG.info("[Feed Recalculation] Completed for user_id = " + request.userId);

        Response response = new Response();
        response.feed = feed;
        return response;
    }

    public static class Request
    {
        public String userId;
    }

    public static class Response
    {
        public Feed feed;
    }
}
