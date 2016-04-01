package com.clanout.application.framework.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfManager
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String DEPLOYMENT_ENVIRONMENT_KEY = "DEPLOYMENT_ENVIRONMENT";
    private static final String DEPLOYMENT_ENVIRONMENT_DEBUG_VALUE = "DEBUG";

    private static boolean IS_DEBUG_ENVIRONMENT;

    public static void init()
    {
        String deploymentEnvironment = System.getenv(DEPLOYMENT_ENVIRONMENT_KEY);
        IS_DEBUG_ENVIRONMENT = (deploymentEnvironment != null && deploymentEnvironment.equalsIgnoreCase(DEPLOYMENT_ENVIRONMENT_DEBUG_VALUE));

        if (IS_DEBUG_ENVIRONMENT)
        {
            LOG.debug("[Deployment Environment : DEBUG]");
        }
        else
        {
            LOG.debug("[Deployment Environment : PRODUCTION]");
        }
    }

    public static boolean isDebugEnv()
    {
        return IS_DEBUG_ENVIRONMENT;
    }
}
