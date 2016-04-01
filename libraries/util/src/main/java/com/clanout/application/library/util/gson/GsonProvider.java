package com.clanout.application.library.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.OffsetDateTime;

public class GsonProvider
{
    private static GsonBuilder gsonBuilder;
    private static Gson gson;

    public static void init()
    {
        gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeParser.Serializer())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeParser.Deserializer());

        gson = gsonBuilder.create();
    }

    private GsonProvider(){}

    public static GsonBuilder getGsonBuilder()
    {
        return gsonBuilder;
    }

    public static Gson getGson()
    {
        return gson;
    }
}
