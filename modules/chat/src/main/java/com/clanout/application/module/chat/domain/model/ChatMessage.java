package com.clanout.application.module.chat.domain.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class ChatMessage
{
    private String id;
    private String planId;
    private String senderId;
    private String senderName;
    private OffsetDateTime timestamp;
    private String message;

    public ChatMessage(String planId, String senderId, String senderName, String message)
    {
        id = planId + "_" + System.nanoTime();
        this.planId = planId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public String getId()
    {
        return id;
    }

    public String getPlanId()
    {
        return planId;
    }

    public String getSenderId()
    {
        return senderId;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public OffsetDateTime getTimestamp()
    {
        return timestamp;
    }

    public String getMessage()
    {
        return message;
    }
}
