package com.clanout.application.module.plan.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.*;
import com.clanout.application.module.plan.domain.observer.PlanModuleObservers;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.use_case.FetchFriends;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.*;

@ModuleScope
public class FanoutService
{
    private static Logger LOG = LogManager.getRootLogger();

    private PlanModuleObservers observers;

    private FetchFriends fetchFriends;
    private FeedRepository feedRepository;

    @Inject
    public FanoutService(PlanModuleObservers observers, FetchFriends fetchFriends, FeedRepository feedRepository)
    {
        this.observers = observers;
        this.fetchFriends = fetchFriends;
        this.feedRepository = feedRepository;
    }

    public void onCreate(Plan plan)
    {
        /*
         ** OPEN : Add plan to the feed of all the friends of the creator in the plan visibility zones
         ** SECRET : Do nothing
         */

        Map<String, FanoutEffect> usersAffected = new HashMap<>();

        if (plan.getType() == Type.OPEN)
        {
            String creatorId = plan.getCreatorId();

            FetchFriends.Request request = new FetchFriends.Request();
            request.userId = creatorId;
            FetchFriends.Response response = fetchFriends.execute(request);
            List<Friend> friends = response.friends;

            List<String> visibilityZones = plan.getVisibilityZones();

            for (Friend friend : friends)
            {
                if (visibilityZones.contains(friend.getLocationZone()))
                {
                    plan.setRsvp(Rsvp.DEFAULT);
                    plan.setStatus("");
                    plan.setFriends(Arrays.asList(creatorId));
                    plan.setInviter(new ArrayList<>());
                    plan.setInvitee(new ArrayList<>());

                    feedRepository.add(friend.getUserId(), plan);
                    feedRepository.markFeedUpdated(friend.getUserId());
                    usersAffected.put(friend.getUserId(), FanoutEffect.PLAN_ADDED);
                }
            }
        }

        observers.onPlanCreated(plan, new ArrayList<>(usersAffected.keySet()));

        LOG.info("[FANNING:CREATE] (" + plan.getId() + ", " + plan.getCreatorId() + ") " + usersAffected.toString());
    }

    public void onDelete(String userId, Plan plan)
    {
        /*
         ** OPEN : Remove plan from the feed of all the attendees, their friends, and people they invited
         ** SECRET : Remove plan from the feed of all the attendees, and people they invited
         */

        Map<String, FanoutEffect> usersAffected = new HashMap<>();

        String planId = plan.getId();
        Type planType = plan.getType();
        List<Attendee> attendees = plan.getAttendees();

        for (Attendee attendee : attendees)
        {
            try
            {
                plan = feedRepository.fetch(attendee.getId(), planId);
            }
            catch (PlanNotFoundException e)
            {
                continue;
            }

            // Remove from this attendee's feed
            feedRepository.remove(attendee.getId(), planId);
            usersAffected.put(attendee.getId(), FanoutEffect.PLAN_REMOVED);

            // Remove from the feed of all the invitees for this attendee
            List<String> invitees = plan.getInvitee();
            for (String invitee : invitees)
            {
                feedRepository.remove(invitee, planId);
                usersAffected.put(attendee.getId(), FanoutEffect.PLAN_REMOVED);
            }

            // If the plan is open, remove from the feed of all the friends of this attendee
            if (planType == Type.OPEN)
            {
                FetchFriends.Request request = new FetchFriends.Request();
                request.userId = attendee.getId();
                FetchFriends.Response response = fetchFriends.execute(request);
                List<Friend> friends = response.friends;

                for (Friend friend : friends)
                {
                    feedRepository.remove(friend.getUserId(), planId);
                    usersAffected.put(friend.getUserId(), FanoutEffect.PLAN_REMOVED);
                }
            }
        }

        for (Map.Entry<String, FanoutEffect> userAffected : usersAffected.entrySet())
        {
            if (!userAffected.getKey().equals(userId))
            {
                feedRepository.markFeedUpdated(userAffected.getKey());
            }
        }

        observers.onPlanDeleted(plan, new ArrayList<>(usersAffected.keySet()));

        LOG.info("[FANNING:DELETE] (" + plan.getId() + ", " + plan.getCreatorId() + ") " + usersAffected.toString());
    }

    public void onUpdate(String planId, String userId, String description, OffsetDateTime startTime,
                         OffsetDateTime endTime, Location location)
    {
        observers.onPlanUpdated(planId, userId, description, startTime, endTime, location);
    }

    public void onRsvpUpdated(String userId, String planId, Rsvp rsvp)
    {
        /*
         ** OPEN : if plan is visible to friends then add this user to "friends_going" list otherwise add plan to friend's feed
         ** SECRET : Add this user to the "friends_going" for all the friends already attending
         */

        if (rsvp != Rsvp.YES && rsvp != Rsvp.NO)
        {
            return;
        }

        Map<String, FanoutEffect> usersAffected = new HashMap<>();

        Plan plan = null;
        Type planType = null;
        try
        {
            plan = feedRepository.fetch(userId, planId);
            planType = plan.getType();
        }
        catch (PlanNotFoundException e)
        {
            return;
        }

        FetchFriends.Request request = new FetchFriends.Request();
        request.userId = userId;
        FetchFriends.Response response = fetchFriends.execute(request);
        List<Friend> friends = response.friends;

        List<String> visibilityZones = plan.getVisibilityZones();

        for (Friend friend : friends)
        {
            if (visibilityZones.contains(friend.getLocationZone()))
            {
                try
                {
                    // Already visible to the friend
                    plan = feedRepository.fetch(friend.getUserId(), planId);

                    if (rsvp == Rsvp.YES)
                    {
                        // Add this user to "friends_going" list for this friend
                        if (!plan.getFriends().contains(userId))
                        {
                            plan.getFriends().add(userId);
                            feedRepository.add(friend.getUserId(), plan);
                            usersAffected.put(friend.getUserId(), FanoutEffect.PLAN_UPDATED);
                        }
                    }
                    else
                    {
                        // Remove this user from "friends_going" list for this friend
                        plan.getFriends().remove(userId);
                        boolean shouldRemove = (plan.getRsvp() != Rsvp.YES) && (plan.getInviter().isEmpty()) &&
                                (plan.getFriends().isEmpty());

                        // Remove the plan from friend's feed if there is not other connection
                        if (shouldRemove)
                        {
                            feedRepository.remove(friend.getUserId(), planId);
                            usersAffected.put(friend.getUserId(), FanoutEffect.PLAN_REMOVED);
                        }
                        else
                        {
                            feedRepository.add(friend.getUserId(), plan);
                            usersAffected.put(friend.getUserId(), FanoutEffect.PLAN_UPDATED);
                        }

                    }
                }
                catch (PlanNotFoundException e)
                {
                    // Initially not visible to friend and plan is open
                    if (planType == Type.OPEN && rsvp == Rsvp.YES)
                    {
                        plan.setRsvp(Rsvp.DEFAULT);
                        plan.setStatus("");
                        plan.setFriends(Arrays.asList(userId));
                        plan.setInviter(new ArrayList<>());
                        plan.setInvitee(new ArrayList<>());

                        // add to friend's feed
                        feedRepository.add(friend.getUserId(), plan);
                        usersAffected.put(friend.getUserId(), FanoutEffect.PLAN_ADDED);
                    }
                }
            }
        }

        for (Map.Entry<String, FanoutEffect> userAffected : usersAffected.entrySet())
        {
            feedRepository.markFeedUpdated(userAffected.getKey());
        }

        observers.onRsvpUpdated(userId, plan, rsvp, usersAffected);

        LOG.info("[FANNING:RSVP] (" + plan.getId() + ", " + plan.getCreatorId() + ", " + rsvp +
                         ") " + usersAffected.toString());
    }

    public void onInvite(String userId, String planId, List<String> to)
    {
        Map<String, FanoutEffect> usersAffected = new HashMap<>();

        Plan plan = null;
        try
        {
            plan = feedRepository.fetch(userId, planId);
        }
        catch (PlanNotFoundException e)
        {
            return;
        }

        for (String invitee : to)
        {
            try
            {
                plan = feedRepository.fetch(invitee, planId);

                // Plan Already visible => Add user to inviter list
                feedRepository.addInviter(invitee, planId, userId);
                usersAffected.put(invitee, FanoutEffect.PLAN_UPDATED);
            }
            catch (PlanNotFoundException e)
            {
                // Plan Not visible => Add to feed
                plan.setRsvp(Rsvp.DEFAULT);
                plan.setStatus("");
                plan.setFriends(Arrays.asList(userId));
                plan.setInviter(Arrays.asList(userId));
                plan.setInvitee(new ArrayList<>());

                feedRepository.add(invitee, plan);
                usersAffected.put(invitee, FanoutEffect.PLAN_ADDED);
            }
        }

        for (Map.Entry<String, FanoutEffect> userAffected : usersAffected.entrySet())
        {
            feedRepository.markFeedUpdated(userAffected.getKey());
        }

        observers.onInvite(userId, plan, usersAffected);

        LOG.info("[FANNING:INVITE] (" + plan.getId() + ", " + plan.getCreatorId() + ") " + usersAffected.toString());
    }

    public enum FanoutEffect
    {
        PLAN_ADDED,
        PLAN_REMOVED,
        PLAN_UPDATED;
    }
}
