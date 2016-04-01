package com.clanout.application.framework.conf.implementation;


import com.clanout.application.framework.conf.Conf;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class ApacheConf implements Conf
{
    private Configuration configuration;

    public ApacheConf(URL confFile)
    {
        try
        {
            configuration = new PropertiesConfiguration(confFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            configuration = null;
        }
    }

    @Override
    public String get(String key)
    {
        if (configuration != null)
        {
            return configuration.getString(key);
        }
        else
        {
            return null;
        }
    }

    @Override
    public List<String> getList(String key)
    {
        if (configuration != null)
        {
            return Arrays.asList(configuration.getStringArray(key));
        }
        else
        {
            return null;
        }
    }
}
