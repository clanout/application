package com.clanout.application.module.user.domain.service;

import com.clanout.application.framework.di.ModuleScope;

import javax.inject.Inject;
import java.util.UUID;

@ModuleScope
public class UserService
{
    @Inject
    public UserService()
    {
    }

    public String generateUserId()
    {
        return UUID.randomUUID().toString();
    }
}
