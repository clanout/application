package com.clanout.application.library.postgres;


import com.clanout.application.framework.conf.ConfResource;

public class PostgresConf extends ConfResource
{
    public static final PostgresConf DB = new PostgresConf("db");

    private PostgresConf(String filename)
    {
        super(filename);
    }
}
