package com.clanout.application.module.notification.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.notification.domain.observer.NotificationModuleSubscriptions;
import com.clanout.application.module.notification.domain.use_case.RegisterGcmToken;
import dagger.Component;

@ModuleScope
@Component(modules = NotificationDependencyProvider.class)
interface NotificationDependencyInjector
{
    NotificationModuleSubscriptions subscriptions();

    RegisterGcmToken registerGcmToken();
}
