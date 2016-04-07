package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.CollectionUtils;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ModuleScope
public class FetchRegisteredContacts
{
    private UserRepository userRepository;

    @Inject
    public FetchRegisteredContacts(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public Response execute(Request request)
    {
        List<Friend> registeredContacts = new ArrayList<>();

        if (!CollectionUtils.isNullOrEmpty(request.mobileHash))
        {
            registeredContacts = userRepository.fetchRegisteredContacts(request.mobileHash);
        }

        String locationZone = request.locationZone;
        if (locationZone != null)
        {
            registeredContacts = registeredContacts
                    .stream()
                    .filter(registeredContact -> registeredContact.getLocationZone().equals(locationZone))
                    .collect(Collectors.toList());
        }

        Response response = new Response();
        response.registeredContacts = registeredContacts;
        return response;
    }

    public static class Request
    {
        public String userId;
        public List<String> mobileHash;
        public String locationZone;
    }

    public static class Response
    {
        public List<Friend> registeredContacts;
    }
}
