package com.clanout.application.module.user.domain.repository;

import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.model.User;

import java.util.List;

public interface UserRepository
{
    void create(User user);

    User fetch(String userId);

    User fetch(String usernameType, String username);

    List<String> fetch(String usernameType, List<String> usernames);

    void addFriends(String userId, List<String> friends);

    List<Friend> fetchFriends(String userId);

    boolean updateLocation(String userId, String locationZone);

    void updateMobileNumber(String userId, String mobileNumber);

    void block(String userId, List<String> toBlock);

    void unblock(String userId, List<String> toUnblock);
}
