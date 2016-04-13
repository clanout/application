package com.clanout.application.module.user.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import org.apache.commons.codec.binary.Base64;

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
        String seed = UUID.randomUUID().toString() + System.nanoTime();
        return Base64.encodeBase64URLSafeString(seed.getBytes()).substring(0, 25);
    }
}
