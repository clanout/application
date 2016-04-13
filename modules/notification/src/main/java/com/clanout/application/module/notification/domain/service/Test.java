package com.clanout.application.module.notification.domain.service;

import com.clanout.application.library.postgres.PostgresLibrary;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.notification.data.gcm.PostgresNotificationRepository;
import com.clanout.application.module.notification.domain.model.Notification;
import com.clanout.application.module.notification.domain.model.Type;
import com.clanout.application.module.notification.domain.repository.NotificationRepository;

import java.util.Arrays;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        GsonProvider.init();
        PostgresLibrary postgresLibrary = new PostgresLibrary();

        NotificationRepository notificationRepository = new PostgresNotificationRepository();
        String userId1 = "ZGQ1YmRjMWUtNTk4Zi00MThkLWJjNGItMjNmYmNhODZmMWY3NTQ0MTM4MDU4OTUwODk";
        String userId2 = "YThlNWNlNDgtODQyZC00YWZhLWE0YjEtYWYwNDE3ZmZiNzZiODEwMzI1MzA5NzEwNjE";

        Notification notification = new Notification(Type.FRIEND_NEW);
        notification.addParameter("user_id", userId2);
        notification.addParameter("user_name", notificationRepository.getUserName(userId2));

        NotificationService notificationService = new NotificationService(null, notificationRepository);
        notificationService.sendMulticast(Arrays.asList(userId1), notification);

        postgresLibrary.destroy();
    }
}
