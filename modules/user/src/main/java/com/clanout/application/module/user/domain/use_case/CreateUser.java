package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.location.domain.model.LocationZone;
import com.clanout.application.module.user.domain.exception.CreateUserException;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.repository.UserRepository;
import com.clanout.application.module.user.domain.service.UserService;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@ModuleScope
public class CreateUser
{
    private UserService userService;
    private UserRepository userRepository;

    @Inject
    public CreateUser(UserService userService, UserRepository userRepository)
    {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public Response execute(Request request) throws InvalidFieldException, CreateUserException
    {
        if (StringUtils.isNullOrEmpty(request.firstname))
        {
            throw new InvalidFieldException("first name");
        }

        if (StringUtils.isNullOrEmpty(request.lastname))
        {
            throw new InvalidFieldException("last name");
        }

        if (StringUtils.isNullOrEmpty(request.gender))
        {
            throw new InvalidFieldException("gender");
        }

        if (StringUtils.isNullOrEmpty(request.username))
        {
            throw new InvalidFieldException("username");
        }

        if (StringUtils.isNullOrEmpty(request.usernameType))
        {
            throw new InvalidFieldException("username type");
        }

        if (StringUtils.isNullOrEmpty(request.locationZone))
        {
            request.locationZone = LocationZone.UNKNOWN_ZONE.getZoneCode();
        }

        User user = new User();
        user.setUserId(userService.generateUserId());
        user.setFirstname(request.firstname);
        user.setLastname(request.lastname);
        user.setEmail(request.email);
        user.setMobileNumber(request.mobileNumber);
        user.setGender(request.gender);
        user.setUsernameType(request.usernameType);
        user.setUsername(request.username);
        user.setLocationZone(request.locationZone);
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        try
        {
            userRepository.create(user);
        }
        catch (Exception e)
        {
            throw new CreateUserException();
        }

        Response response = new Response();
        response.user = user;
        return response;
    }

    public static class Request
    {
        public String firstname;
        public String lastname;
        public String email;
        public String mobileNumber;
        public String gender;
        public String username;
        public String usernameType;
        public String locationZone;
    }

    public static class Response
    {
        public User user;
    }
}
