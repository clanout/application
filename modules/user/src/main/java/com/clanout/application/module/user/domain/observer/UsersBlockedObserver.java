package com.clanout.application.module.user.domain.observer;

import java.util.List;

public interface UsersBlockedObserver
{
    void onUsersBlocked(String userId, List<String> blocked);
}
