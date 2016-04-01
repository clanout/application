package com.clanout.application.framework.conf;


import com.clanout.application.framework.conf.implementation.ApacheConf;

public class ConfLoader
{
    public static Conf getConf(ConfResource confResource)
    {
        if (ConfManager.isDebugEnv())
        {
            return new ApacheConf(confResource.debugResource);
        }
        else
        {
            return new ApacheConf(confResource.productionResource);
        }
    }
}
