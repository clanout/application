package com.clanout.application.module.auth.domain.repository;

import com.clanout.application.module.auth.domain.model.User;

public interface UserRepository
{
    void create(User user);

    User fetch(String usernameType, String username);
}
