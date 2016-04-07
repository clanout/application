package com.clanout.application.module.notification.domain.repository;

import java.util.List;

public interface NotificationRepository
{
    void register(String userId, String gcmToken);

    List<String> fetchTokens(List<String> userIds);

    String getUserName(String userId);
}
