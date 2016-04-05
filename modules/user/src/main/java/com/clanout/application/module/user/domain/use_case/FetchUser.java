package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;

@ModuleScope
public class FetchUser
{
    private UserRepository userRepository;

    @Inject
    public FetchUser(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public Response execute(Request request) throws InvalidFieldException
    {
        User user = userRepository.fetch(request.userId);
        if (user == null)
        {
            throw new InvalidFieldException("user id");
        }

        Response response = new Response();
        response.user = user;
        return response;
    }

    public static class Request
    {
        public String userId;
    }

    public static class Response
    {
        public User user;
    }
}
