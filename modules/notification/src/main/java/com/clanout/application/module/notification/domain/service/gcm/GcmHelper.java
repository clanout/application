package com.clanout.application.module.notification.domain.service.gcm;

import com.clanout.application.framework.conf.ConfLoader;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.notification.domain.conf.NotificationConf;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import java.util.concurrent.TimeUnit;

public class GcmHelper
{
    private static RestAdapter restAdapter;

    private static final String GCM_URL = ConfLoader.getConf(NotificationConf.NOTIFICATION).get("gcm.url");
    private static final String GCM_KEY = ConfLoader.getConf(NotificationConf.NOTIFICATION).get("gcm.api_key");

    static
    {
        restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(getOkHttpClient()))
                .setConverter(new GsonConverter(GsonProvider.getGson()))
                .setEndpoint(GCM_URL)
                .setRequestInterceptor(requestFacade -> {
                    requestFacade.addHeader("Content-Type", "application/json");
                    requestFacade.addHeader("Authorization", "key=" + GCM_KEY);
                })
                .build();
    }

    public static GcmApi getApi()
    {
        return restAdapter.create(GcmApi.class);
    }

    private static OkHttpClient getOkHttpClient()
    {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(45, TimeUnit.SECONDS);
        return client;
    }
}
