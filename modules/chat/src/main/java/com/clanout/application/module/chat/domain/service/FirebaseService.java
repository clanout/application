package com.clanout.application.module.chat.domain.service;

import com.squareup.okhttp.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class FirebaseService
{
    public static final String FACEBOOK_USERNAME_TYPE = "FACEBOOK";

    private static Logger LOG = LogManager.getRootLogger();

    private static final String BASE_URL = "https://clanout-1275.firebaseio.com/chat/";
    private static final String URL_SUFFIX = ".json";
    public static final MediaType JSON = MediaType.parse("text/plain; charset=utf-8");

    private OkHttpClient okHttpClient;

    @Inject
    public FirebaseService()
    {
        okHttpClient = new OkHttpClient();
    }

    public void post(String chatId, String messageJson)
    {
        Response response = null;
        try
        {
            RequestBody body = RequestBody.create(JSON, messageJson);
            Request request = new Request.Builder()
                    .url(BASE_URL + chatId + URL_SUFFIX)
                    .post(body)
                    .build();

            response = okHttpClient.newCall(request).execute();
            if (response.code() != 200)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            try
            {
                LOG.error("Unable send firebase message [" + response.code() + " : " + response.body().string() + "]");
            }
            catch (Exception e1)
            {
                LOG.error("Unable send firebase message");
            }
        }
    }

    public void delete(String chatId)
    {
        Response response = null;
        try
        {
            Request request = new Request.Builder()
                    .url(BASE_URL + chatId + URL_SUFFIX)
                    .delete()
                    .build();

            response = okHttpClient.newCall(request).execute();
            if (response.code() != 200)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            try
            {
                LOG.error("Unable to delete firebase chatroom [" + response.code() + " : " + response.body().string() + "]");
            }
            catch (Exception e1)
            {
                LOG.error("Unable to delete firebase chatroom");
            }
        }
    }
}
