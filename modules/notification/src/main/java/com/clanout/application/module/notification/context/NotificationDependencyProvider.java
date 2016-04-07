package com.clanout.application.module.notification.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.async.AsyncPool;
import com.clanout.application.module.auth.context.AuthContext;
import com.clanout.application.module.notification.data.gcm.PostgresNotificationRepository;
import com.clanout.application.module.notification.domain.observer.NotificationModuleSubscriptions;
import com.clanout.application.module.notification.domain.repository.NotificationRepository;
import com.clanout.application.module.notification.domain.service.NotificationService;
import com.clanout.application.module.plan.context.PlanContext;
import com.clanout.application.module.user.context.UserContext;
import com.clanout.application.module.user.domain.use_case.FetchFriends;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutorService;

@Module
class NotificationDependencyProvider
{
    private UserContext userContext;
    private AuthContext authContext;
    private PlanContext planContext;

    public NotificationDependencyProvider(UserContext userContext, AuthContext authContext, PlanContext planContext)
    {
        this.userContext = userContext;
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
    public NotificationModuleSubscriptions provideNotificationModuleSubscriptions(ExecutorService backgroundPool,
                                                                                  NotificationService notificationService)
    {
        return new NotificationModuleSubscriptions(backgroundPool, userContext, authContext, planContext, notificationService);
    }

    @Provides
    @ModuleScope
    public NotificationRepository provideGcmRepository()
    {
        return new PostgresNotificationRepository();
    }

    @Provides
    @ModuleScope
    public FetchFriends provideFetchFriends()
    {
        return userContext.fetchFriends();
    }
}
