package com.clanout.application.module.chat.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.async.AsyncPool;
import com.clanout.application.module.auth.context.AuthContext;
import com.clanout.application.module.chat.data.chat.PostgresChatRepository;
import com.clanout.application.module.chat.domain.observer.ChatModuleSubscriptions;
import com.clanout.application.module.chat.domain.repository.ChatRepository;
import com.clanout.application.module.chat.domain.service.ChatService;
import com.clanout.application.module.plan.context.PlanContext;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutorService;

@Module
class ChatDependencyProvider
{
    private AuthContext authContext;
    private PlanContext planContext;

    public ChatDependencyProvider(AuthContext authContext, PlanContext planContext)
    {
        this.authContext = authContext;
        this.planContext = planContext;
    }

    @Provides
    @ModuleScope
    public ExecutorService provideBackgroundPool()
    {
        return AsyncPool.getInstance().getBackgroundPool();
    }

    @Provides
    @ModuleScope
    public ChatModuleSubscriptions provideChatModuleSubscriptions(ExecutorService backgroundPool,
                                                                  ChatService chatService)
    {
        return new ChatModuleSubscriptions(backgroundPool, authContext, planContext, chatService);
    }

    @Provides
    @ModuleScope
    public ChatRepository provideGcmRepository()
    {
        return new PostgresChatRepository();
    }
}
