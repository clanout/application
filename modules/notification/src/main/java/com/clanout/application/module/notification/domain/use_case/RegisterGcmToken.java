package com.clanout.application.module.notification.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.notification.domain.repository.NotificationRepository;

import javax.inject.Inject;

@ModuleScope
public class RegisterGcmToken
{
    private NotificationRepository notificationRepository;

    @Inject
    public RegisterGcmToken(NotificationRepository notificationRepository)
    {
        this.notificationRepository = notificationRepository;
    }

    public void execute(Request request) throws InvalidFieldException
    {
        if (StringUtils.isNullOrEmpty(request.gcmToken))
        {
            throw new InvalidFieldException("gcm token");
        }

        notificationRepository.register(request.userId, request.gcmToken);
    }

    public static class Request
    {
        public String userId;
        public String gcmToken;
    }
}
