package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.user.domain.exception.InvalidUserFieldException;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.List;

@ModuleScope
public class AddFriends
{
    private UserRepository userRepository;

    @Inject
    public AddFriends(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public void execute(Request request) throws InvalidUserFieldException
    {
        if (StringUtils.isNullOrEmpty(request.friendUsernameType))
        {
            throw new InvalidUserFieldException("friends username type");
        }

        if (request.usernames == null)
        {
            throw new InvalidUserFieldException("friends");
        }

        if (!request.usernames.isEmpty())
        {
            List<String> userIds = userRepository.fetch(request.friendUsernameType, request.usernames);
            userRepository.addFriends(request.userId, userIds);
        }
    }

    public static class Request
    {
        public String userId;
        public String friendUsernameType;
        public List<String> usernames;
    }
}
