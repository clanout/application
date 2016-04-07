package com.clanout.application.module.notification.domain.service.sms;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SmsApi
{
    @GET("/?template_name=Charlie&response_format=json")
    Response sendBetaInvitation(@Query("destination") String destination,
                                @Query("templateParameters[A]") String inviterName,
                                @Query("templateParameters[B]") String planTitle,
                                @Query("templateParameters[C]") String webUrl);
}
