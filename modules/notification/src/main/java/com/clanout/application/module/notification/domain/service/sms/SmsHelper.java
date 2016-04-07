package com.clanout.application.module.notification.domain.service.sms;

import com.clanout.application.framework.conf.ConfLoader;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.notification.domain.conf.NotificationConf;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import java.util.concurrent.TimeUnit;

public class SmsHelper
{
    private static RestAdapter restAdapter;

    private static final String SMS_URL = ConfLoader.getConf(NotificationConf.NOTIFICATION).get("sms.url");
    private static final String SMS_SENDER_ID = ConfLoader.getConf(NotificationConf.NOTIFICATION).get("sms.sender_id");
    private static final String SMS_USERNAME = ConfLoader.getConf(NotificationConf.NOTIFICATION).get("sms.username");
    private static final String SMS_PASSWORD = ConfLoader.getConf(NotificationConf.NOTIFICATION).get("sms.password");

    static
    {
        restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(getOkHttpClient()))
                .setEndpoint(SMS_URL)
                .setConverter(new GsonConverter(GsonProvider.getGson()))
                .setRequestInterceptor(requestFacade -> {
                    requestFacade.addQueryParam("sender_id", SMS_SENDER_ID);
                    requestFacade.addQueryParam("username", SMS_USERNAME);
                    requestFacade.addQueryParam("password", SMS_PASSWORD);
                })
                .build();
    }

    public static SmsApi getApi()
    {
        return restAdapter.create(SmsApi.class);
    }

    private static OkHttpClient getOkHttpClient()
    {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(45, TimeUnit.SECONDS);
        return client;
    }
}
