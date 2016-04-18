package com.clanout.application.module.chat.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.library.xmpp.XmppManager;
import com.clanout.application.module.chat.domain.model.ChatMessage;
import com.clanout.application.module.chat.domain.repository.ChatRepository;
import com.clanout.application.module.plan.domain.model.Location;
import com.clanout.application.module.plan.domain.model.Plan;

import javax.inject.Inject;
import java.time.OffsetDateTime;

@ModuleScope
public class ChatService
{
    private static final String ADMIN_NICK = XmppManager.getAdminNickname();

    private ChatRepository chatRepository;

    @Inject
    public ChatService(ChatRepository chatRepository)
    {
        this.chatRepository = chatRepository;
    }

    public void userRegistered(String userId)
    {
        XmppManager.createUser(userId);
    }

    public void planCreated(String planId)
    {
        XmppManager.createChatroom(planId);
    }

    public void userJoinedPlan(Plan plan, String userId)
    {
        String name = chatRepository.getUserName(userId);
        String message = "rsvp:" + name + ":YES";
        send(plan.getId(), plan.getTitle(), message);
    }

    public void userLeftPlan(Plan plan, String userId)
    {
        String name = chatRepository.getUserName(userId);
        String message = "rsvp:" + name + ":NO";
        send(plan.getId(), plan.getTitle(), message);
    }

    public void planStartTimeUpdated(Plan plan, String userId, OffsetDateTime updatedStartTime)
    {
        String name = chatRepository.getUserName(userId);
        String message = "start_time:" + name + ":" + updatedStartTime.toString();
        send(plan.getId(), plan.getTitle(), message);
    }

    public void planLocationUpdated(Plan plan, String userId, Location location)
    {
        String name = chatRepository.getUserName(userId);
        String locationName = location.getName();
        String message = null;
        if (StringUtils.isNullOrEmpty(locationName))
        {
            message = "location:" + name + ":0";
        }
        else
        {
            message = "location:" + name + ":" + locationName;
        }
        send(plan.getId(), plan.getTitle(), message);
    }

    public void planDescriptionUpdated(Plan plan, String userId, String description)
    {
        String name = chatRepository.getUserName(userId);
        String message = null;
        if (StringUtils.isNullOrEmpty(description))
        {
            message = "description:" + name + ":0";
        }
        else
        {
            message = "description:" + name + ":" + description;
        }
        send(plan.getId(), plan.getTitle(), message);
    }

    public void planInvitationResponse(String planId, String userId, String invitationResponse)
    {
        String name = chatRepository.getUserName(userId);
        String message = "invitation_response:" + name;
        send(planId, null, message);
    }

    private void send(String planId, String planTitle, String message)
    {
        ChatMessage chatMessage = new ChatMessage(planId, planTitle, ADMIN_NICK, ADMIN_NICK, message);
        XmppManager.sendMessage(chatMessage.getPlanId(), GsonProvider.getGson().toJson(chatMessage));
    }
}
