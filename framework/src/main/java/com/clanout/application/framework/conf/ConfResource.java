package com.clanout.application.framework.conf;

import java.net.URL;

public abstract class ConfResource
{
    private static final String PRODUCTION_RESOURCE_POSTFIX = ".conf";
    private static final String DEBUG_RESOURCE_POSTFIX = ".debug.conf";

    protected URL productionResource;
    protected URL debugResource;

    protected ConfResource(String filename)
    {
        productionResource = this.getClass().getResource("/" + filename + PRODUCTION_RESOURCE_POSTFIX);
        debugResource = this.getClass().getResource("/" + filename + DEBUG_RESOURCE_POSTFIX);

        if(debugResource == null)
        {
            debugResource = productionResource;
        }
    }
}
