package com.clanout.application.library.redis;


import com.clanout.application.framework.conf.ConfResource;

public class RedisConf extends ConfResource
{
    public static final RedisConf DB = new RedisConf("redis");

    private RedisConf(String filename)
    {
        super(filename);
    }
}
