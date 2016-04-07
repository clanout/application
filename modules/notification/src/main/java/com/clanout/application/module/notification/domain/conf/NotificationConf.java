package com.clanout.application.module.notification.domain.conf;


import com.clanout.application.framework.conf.ConfResource;

public class NotificationConf extends ConfResource
{
    public static final NotificationConf GCM = new NotificationConf("notification");

    private NotificationConf(String filename)
    {
        super(filename);
    }
}
