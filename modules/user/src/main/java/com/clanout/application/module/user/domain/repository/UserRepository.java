package com.clanout.application.module.user.domain.repository;

import com.clanout.application.module.user.domain.model.User;

public interface UserRepository
{
    void create(User user);

    User fetch(String usernameType, String username);
}
