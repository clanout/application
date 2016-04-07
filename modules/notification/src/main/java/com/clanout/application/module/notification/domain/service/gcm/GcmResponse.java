package com.clanout.application.module.notification.domain.service.gcm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GcmResponse
{
    /* Topic Messaging Response */

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("error")
    private String error;

    public String getMessageId()
    {
        return messageId;
    }

    public String getError()
    {
        return error;
    }


    /* Multicast Messaging Response */

    @SerializedName("multicast_id")
    private String musticastId;

    @SerializedName("success")
    private int successCount;

    @SerializedName("failure")
    private int failureCount;

    @SerializedName("canonical_ids")
    private int canonicalIdCount;

    @SerializedName("results")
    private List<Result> results;

    public String getMusticastId()
    {
        return musticastId;
    }

    public int getSuccessCount()
    {
        return successCount;
    }

    public int getFailureCount()
    {
        return failureCount;
    }

    public int getCanonicalIdCount()
    {
        return canonicalIdCount;
    }

    public List<Result> getResults()
    {
        return results;
    }

    public static class Result
    {
        @SerializedName("message_id")
        private String messageId;

        @SerializedName("registration_id")
        private String registrationId;

        @SerializedName("error")
        private String error;

        public String getMessageId()
        {
            return messageId;
        }

        public String getRegistrationId()
        {
            return registrationId;
        }

        public String getError()
        {
            return error;
        }
    }
}
