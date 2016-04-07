package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.module.location.domain.model.LocationZone;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ModuleScope
public class FetchFriends
{
    private UserRepository userRepository;

    @Inject
    public FetchFriends(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public Response execute(Request request) throws InvalidFieldException
    {
        if(request.locationZone != null && request.locationZone.equals(LocationZone.UNKNOWN_ZONE.getZoneCode()))
        {
            throw new InvalidFieldException("location zone");
        }

        List<Friend> friends = userRepository.fetchFriends(request.userId);

        String locationZone = request.locationZone;
        if (locationZone != null)
        {
            friends = friends
                    .stream()
                    .filter(friend -> friend.getLocationZone().equals(locationZone))
                    .collect(Collectors.toList());
        }

        Response response = new Response();
        response.friends = friends;
        return response;
    }

    public static class Request
    {
        public String userId;
        public String locationZone;
    }

    public static class Response
    {
        public List<Friend> friends;
    }
}
