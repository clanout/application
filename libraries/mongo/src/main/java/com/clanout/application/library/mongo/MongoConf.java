package com.clanout.application.library.mongo;


import com.clanout.application.framework.conf.ConfResource;

public class MongoConf extends ConfResource
{
    public static final MongoConf DB = new MongoConf("mongo");

    private MongoConf(String filename)
    {
        super(filename);
    }
}
