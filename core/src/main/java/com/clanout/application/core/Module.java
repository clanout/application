package com.clanout.application.core;


import com.clanout.application.framework.module.Context;

public enum Module
{
    ;

    private Class<? extends Context> contextClass;

    Module(Class<? extends Context> contextClass)
    {
        this.contextClass = contextClass;
    }

    public Class<? extends Context> getContextClass()
    {
        return contextClass;
    }
}
