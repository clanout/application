package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.user.domain.exception.InvalidUserFieldException;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;

@ModuleScope
public class FetchUserFromUsername
{
    private UserRepository userRepository;

    @Inject
    public FetchUserFromUsername(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public Response execute(Request request) throws InvalidUserFieldException
    {
        if (StringUtils.isNullOrEmpty(request.usernameType))
        {
            throw new InvalidUserFieldException("username_type");
        }

        if (StringUtils.isNullOrEmpty(request.username))
        {
            throw new InvalidUserFieldException("username");
        }

        User user = userRepository.fetch(request.usernameType, request.username);

        Response response = new Response();
        response.user = user;
        return response;
    }

    public static class Request
    {
        public String usernameType;
        public String username;
    }

    public static class Response
    {
        public User user;
    }
}
