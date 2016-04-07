package com.clanout.application.module.notification.domain.model;

import java.util.HashMap;
import java.util.Map;

public class Notification
{
    private Type type;
    private Map<String, String> data;

    public Notification(Type type)
    {
        this.type = type;
        data = new HashMap<>();
        data.put("notification_id", String.valueOf((int) (Math.random() * 1000000000)));
    }

    public Type getType()
    {
        return type;
    }

    public Map<String, String> getData()
    {
        return data;
    }

    public void addParameter(String key, String value)
    {
        data.put(key, value);
    }
}
