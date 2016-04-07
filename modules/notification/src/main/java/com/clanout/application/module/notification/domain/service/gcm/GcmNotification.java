package com.clanout.application.module.notification.domain.service.gcm;

import com.clanout.application.module.notification.domain.model.Notification;
import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class GcmNotification
{
    @SerializedName("to")
    private String to;

    @SerializedName("registration_ids")
    private List<String> registrationIds;

    @SerializedName("data")
    private Data data;

    public GcmNotification(String to, List<String> registrationIds, Data data)
    {
        this.to = to;
        this.registrationIds = registrationIds;
        this.data = data;
    }

    public static class Data
    {
        @SerializedName("type")
        private String type;

        @SerializedName("message")
        private String message;

        @SerializedName("parameters")
        private Map<String, String> parameters;
    }

    public static class Factory
    {
        private static Logger LOG = LogManager.getRootLogger();

        public static GcmNotification multicastNotification(List<String> registrationIds, Notification notification)
        {
            try
            {
                Data data = new Data();
                data.type = notification.getType().name();
                data.message = "";
                data.parameters = notification.getData();

                return new GcmNotification(null, registrationIds, data);
            }
            catch (Exception e)
            {
                LOG.error("Unable to create multicast notification [" + e.getMessage() + "]");
                return null;
            }
        }

        public static GcmNotification broadcastNotification(String channelId, Notification notification)
        {
            try
            {
                Data data = new Data();
                data.type = notification.getType().name();
                data.message = "";
                data.parameters = notification.getData();

                return new GcmNotification(channelId, null, data);
            }
            catch (Exception e)
            {
                LOG.error("Unable to create broadcast notification [" + e.getMessage() + "]");
                return null;
            }
        }
    }
}
