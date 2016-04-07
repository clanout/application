package com.clanout.application.module.notification.domain.service.gcm;

import retrofit.http.Body;
import retrofit.http.POST;

public interface GcmApi
{
    @POST("/send")
    GcmResponse send(@Body GcmNotification notification);
}
