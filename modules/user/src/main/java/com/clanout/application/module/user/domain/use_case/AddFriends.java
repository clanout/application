package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ModuleScope
public class AddFriends
{
    private UserRepository userRepository;
    private FetchFriends fetchFriends;

    @Inject
    public AddFriends(FetchFriends fetchFriends, UserRepository userRepository)
    {
        this.fetchFriends = fetchFriends;
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

            FetchFriends.Request fetchFriendsRequest = new FetchFriends.Request();
            fetchFriendsRequest.userId = request.userId;
            List<String> friendIds = fetchFriends.execute(fetchFriendsRequest)
                    .friends
                    .stream()
                    .map(Friend::getUserId)
                    .collect(Collectors.toList());

            List<String> toAdd = userIds
                    .stream()
                    .filter(userId -> !friendIds.contains(userId))
                    .collect(Collectors.toList());

            userRepository.addFriends(request.userId, toAdd);
        }
    }

    public static class Request
    {
        public String userId;
        public String friendUsernameType;
        public List<String> usernames;
    }
}
