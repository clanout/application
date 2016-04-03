package com.clanout.application.module.auth.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.auth.domain.exception.CreateUserException;
import com.clanout.application.module.auth.domain.model.User;
import com.clanout.application.module.auth.domain.repository.UserRepository;
import com.clanout.application.module.location.domain.model.LocationZone;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@ModuleScope
public class UserService
{
    private UserRepository userRepository;

    @Inject
    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public User register(User user) throws CreateUserException
    {
        user.setUserId(generateUserId());
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        user.setLocationZone(LocationZone.UNKNOWN_ZONE.getZoneCode());
        user.setIsNew(true);

        try
        {
            userRepository.create(user);
        }
        catch (Exception e)
        {
            throw new CreateUserException();
        }

        return user;
    }

    private String generateUserId()
    {
        return UUID.randomUUID().toString();
    }
}
