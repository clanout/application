package com.clanout.application.module.user.domain.observer;

import java.util.List;

public interface UsersUnblockedObserver
{
    void onUsersUnblocked(String userId, List<String> unblocked);
}
