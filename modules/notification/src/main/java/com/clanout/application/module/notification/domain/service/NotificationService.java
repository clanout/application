package com.clanout.application.module.notification.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.library.util.validation.PhoneValidator;
import com.clanout.application.module.notification.domain.model.Notification;
import com.clanout.application.module.notification.domain.model.Type;
import com.clanout.application.module.notification.domain.repository.NotificationRepository;
import com.clanout.application.module.notification.domain.service.gcm.GcmApi;
import com.clanout.application.module.notification.domain.service.gcm.GcmHelper;
import com.clanout.application.module.notification.domain.service.gcm.GcmNotification;
import com.clanout.application.module.notification.domain.service.gcm.GcmResponse;
import com.clanout.application.module.notification.domain.service.sms.SmsApi;
import com.clanout.application.module.notification.domain.service.sms.SmsHelper;
import com.clanout.application.module.plan.domain.model.Location;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.service.FanoutService;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.use_case.FetchFriends;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.client.Response;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ModuleScope
public class NotificationService
{
    private static Logger LOG = LogManager.getRootLogger();
    private static Gson GSON = GsonProvider.getGson();

    private FetchFriends fetchFriends;
    private NotificationRepository notificationRepository;

    @Inject
    public NotificationService(FetchFriends fetchFriends, NotificationRepository notificationRepository)
    {
        this.fetchFriends = fetchFriends;
        this.notificationRepository = notificationRepository;
    }

    public void newUser(String userId)
    {
        FetchFriends.Request request = new FetchFriends.Request();
        request.userId = userId;
        FetchFriends.Response response = null;
        try
        {
            response = fetchFriends.execute(request);
        }
        catch (InvalidFieldException e)
        {
            return;
        }

        List<String> friendIds = response.friends
                .stream()
                .map(Friend::getUserId)
                .collect(Collectors.toList());

        Notification notification = new Notification(Type.FRIEND_NEW);
        notification.addParameter("user_id", userId);
        notification.addParameter("user_name", notificationRepository.getUserName(userId));

        sendMulticast(friendIds, notification);
    }

    public void userRelocation(String userId, String locationZone)
    {
        FetchFriends.Request request = new FetchFriends.Request();
        request.userId = userId;
        FetchFriends.Response response = null;
        try
        {
            response = fetchFriends.execute(request);
        }
        catch (InvalidFieldException e)
        {
            return;
        }

        List<String> friendIds = response.friends
                .stream()
                .map(Friend::getUserId)
                .collect(Collectors.toList());

        Notification notification = new Notification(Type.FRIEND_RELOCATED);
        notification.addParameter("user_id", userId);
        notification.addParameter("user_name", notificationRepository.getUserName(userId));
        notification.addParameter("location_zone", locationZone);

        sendMulticast(friendIds, notification);
    }

    public void planCreated(Plan plan, List<String> affectedUsers)
    {
        Notification notification = new Notification(Type.PLAN_CREATED);
        notification.addParameter("plan_id", plan.getId());
        notification.addParameter("plan_title", plan.getTitle());
        notification.addParameter("user_id", plan.getCreatorId());
        notification.addParameter("user_name", notificationRepository.getUserName(plan.getCreatorId()));

        sendMulticast(affectedUsers, notification);
    }

    public void planDeleted(Plan plan, List<String> affectedUsers)
    {
        Notification notification = new Notification(Type.PLAN_DELETED);
        notification.addParameter("plan_id", plan.getId());
        notification.addParameter("plan_title", plan.getTitle());
        notification.addParameter("user_id", plan.getCreatorId());
        notification.addParameter("user_name", notificationRepository.getUserName(plan.getCreatorId()));

        sendMulticast(affectedUsers, notification);
    }

    public void planUpdated(Plan plan, String userId, String description, OffsetDateTime startTime,
                            OffsetDateTime endTime, Location location)
    {
        Notification notification = new Notification(Type.PLAN_UPDATED);
        notification.addParameter("plan_id", plan.getId());
        notification.addParameter("plan_title", plan.getTitle());
        notification.addParameter("user_id", userId);
        notification.addParameter("user_name", notificationRepository.getUserName(userId));
        notification.addParameter("is_location_updated", String.valueOf(location != null));
        notification.addParameter("is_time_updated", String.valueOf(startTime != null || endTime != null));
        notification.addParameter("is_description_updated", String.valueOf(description != null));

        sendBroadcast(plan.getId(), notification);
    }

    public void rsvpUpdated(String userId, Plan plan, Rsvp rsvp,
                            Map<String, FanoutService.FanoutEffect> affectedUsers)
    {
        List<String> friendsJoinedPlan = new ArrayList<>();
        List<String> planRemovedFromFeed = new ArrayList<>();
        for (Map.Entry<String, FanoutService.FanoutEffect> affectedUser : affectedUsers.entrySet())
        {
            if (affectedUser.getValue() == FanoutService.FanoutEffect.PLAN_REMOVED)
            {
                planRemovedFromFeed.add(affectedUser.getKey());
            }
            else
            {
                friendsJoinedPlan.add(affectedUser.getKey());
            }
        }

        Notification friendsJoinedNotification = new Notification(Type.PLAN_FRIENDS_JOINED);
        friendsJoinedNotification.addParameter("plan_id", plan.getId());
        friendsJoinedNotification.addParameter("plan_title", plan.getTitle());
        friendsJoinedNotification.addParameter("user_id", userId);
        friendsJoinedNotification.addParameter("user_name", notificationRepository.getUserName(userId));

        sendMulticast(friendsJoinedPlan, friendsJoinedNotification);

        Notification planRemovedFromFeedNotification = new Notification(Type.PLAN_REMOVE_FROM_FEED);
        planRemovedFromFeedNotification.addParameter("plan_id", plan.getId());
        planRemovedFromFeedNotification.addParameter("plan_title", plan.getTitle());
        planRemovedFromFeedNotification.addParameter("user_id", userId);
        planRemovedFromFeedNotification.addParameter("user_name", notificationRepository.getUserName(userId));

        sendMulticast(planRemovedFromFeed, planRemovedFromFeedNotification);
    }

    public void invitation(String userId, Plan plan, Map<String, FanoutService.FanoutEffect> affectedUsers)
    {
        List<String> userIds = new ArrayList<>(affectedUsers.keySet());

        Notification notification = new Notification(Type.PLAN_INVITATION);
        notification.addParameter("plan_id", plan.getId());
        notification.addParameter("plan_title", plan.getTitle());
        notification.addParameter("user_id", userId);
        notification.addParameter("user_name", notificationRepository.getUserName(userId));

        sendMulticast(userIds, notification);
    }

    public void mobileInvitation(String userId, Plan plan, List<String> mobileNumbers)
    {
        System.out.println("HERE");

        SmsApi smsApi = SmsHelper.getApi();
        String name = notificationRepository.getUserName(userId);
        String url = "www.clanout.com";

        for (String mobileNumber : mobileNumbers)
        {
            if (PhoneValidator.isValid(mobileNumber))
            {
                try
                {
                    Response response = smsApi.sendBetaInvitation(mobileNumber, name, plan.getTitle(), url);
                    int httpStatus = response.getStatus();
                    if (httpStatus != 200)
                    {
                        throw new Exception("HTTP Status = " + httpStatus);
                    }

                    LOG.info("[SMS] Sent invitation to " + mobileNumber + "(" + plan.getId() + ":" + plan.getTitle() + ")");
                }
                catch (Exception e)
                {
                    LOG.error("[SMS] Failed to send sms to " + mobileNumber + " [" + e.getMessage() + "]");
                }
            }
        }
    }

    public void blocked(String userId, List<String> blocked)
    {
        Notification notification = new Notification(Type.FRIEND_BLOCKED);
        notification.addParameter("user_id", userId);
        notification.addParameter("user_name", notificationRepository.getUserName(userId));

        sendMulticast(blocked, notification);
    }

    public void unblocked(String userId, List<String> unBlocked)
    {
        Notification notification = new Notification(Type.FRIEND_UNBLOCKED);
        notification.addParameter("user_id", userId);
        notification.addParameter("user_name", notificationRepository.getUserName(userId));

        sendMulticast(unBlocked, notification);
    }

    public void chatUpdate(String planId, String message)
    {
        Notification notification = new Notification(Type.PLAN_CHAT);
        notification.addParameter("plan_id", planId);
        notification.addParameter("message", message);

        sendBroadcast(planId, notification);
    }

    public void statusUpdate(String planId, String userId, String status)
    {
        Notification notification = new Notification(Type.PLAN_STATUS);
        notification.addParameter("plan_id", planId);
        notification.addParameter("user_id", userId);
        notification.addParameter("user_name", notificationRepository.getUserName(userId));
        notification.addParameter("status", status);

        sendBroadcast(planId, notification);
    }


    /* GCM Helpers */
    private void sendMulticast(List<String> userIds, Notification notification)
    {
        List<String> tokens = notificationRepository.fetchTokens(userIds);

        LOG.info("[GCM] Multicast : " + GSON.toJson(notification));
        if (!tokens.isEmpty())
        {
            GcmNotification gcmNotification = GcmNotification.Factory.multicastNotification(tokens, notification);
            GcmApi gcmApi = GcmHelper.getApi();
            GcmResponse response = gcmApi.send(gcmNotification);

            if (response != null)
            {
                LOG.info("[GCM] " + response.getMusticastId() + " : Failure Count = " + response.getFailureCount() + "; Reg. ID update count = " + response.getCanonicalIdCount());

                if (response.getFailureCount() != 0 || response.getCanonicalIdCount() != 0)
                {
                    List<GcmResponse.Result> results = response.getResults();
                    int size = results.size();
                    for (int i = 0; i < size; i++)
                    {
                        GcmResponse.Result result = results.get(i);
                        String userId = userIds.get(i);

                        LOG.info("[GCM] Multicast Error : " + userId + " -> " + GsonProvider.getGson().toJson(result));

                        if (result.getRegistrationId() != null)
                        {
                            notificationRepository.register(userId, result.getRegistrationId());
                        }
                    }
                }
            }
            else
            {
                LOG.error("[GCM] Failed to send multicast notification to " + userIds.toString());
            }
        }
    }

    private void sendBroadcast(String channelId, Notification notification)
    {
        channelId = "/topics/" + channelId;

        GcmNotification gcmNotification = GcmNotification.Factory.broadcastNotification(channelId, notification);
        GcmApi gcmApi = GcmHelper.getApi();
        GcmResponse response = gcmApi.send(gcmNotification);

        LOG.info("[GCM] Broadcast : " + GSON.toJson(notification));

        if (response.getError() == null)
        {
            LOG.info("[GCM] Successfully broadcasted message : " + response.getMessageId());
        }
        else
        {
            LOG.error("[GCM] Failed to broadcast message [" + response.getError() + "]");
        }
    }
}
