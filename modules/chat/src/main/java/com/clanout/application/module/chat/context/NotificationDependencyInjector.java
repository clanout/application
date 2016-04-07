package com.clanout.application.module.chat.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.chat.domain.observer.ChatModuleSubscriptions;
import dagger.Component;

@ModuleScope
@Component(modules = ChatDependencyProvider.class)
interface NotificationDependencyInjector
{
    ChatModuleSubscriptions chatModuleSubscriptions();
}
