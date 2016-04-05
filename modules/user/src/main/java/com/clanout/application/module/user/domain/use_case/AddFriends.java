package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
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

    public void execute(Request request) throws InvalidFieldException
    {
        if (StringUtils.isNullOrEmpty(request.friendUsernameType))
        {
            throw new InvalidFieldException("friends username type");
        }

        if (request.usernames == null)
        {
            throw new InvalidFieldException("friends");
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
