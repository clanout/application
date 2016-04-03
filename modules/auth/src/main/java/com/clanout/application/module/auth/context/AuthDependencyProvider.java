package com.clanout.application.module.auth.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.async.AsyncPool;
import com.clanout.application.module.auth.data.postgres.PostgresTokenRepository;
import com.clanout.application.module.auth.data.postgres.PostgresUserRepository;
import com.clanout.application.module.auth.domain.repository.TokenRepository;
import com.clanout.application.module.auth.domain.repository.UserRepository;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutorService;

@Module
class AuthDependencyProvider
{
    @Provides
    @ModuleScope
    public ExecutorService backgroundPool()
    {
        return AsyncPool.getInstance().getBackgroundPool();
    }

    @Provides
    @ModuleScope
    public TokenRepository provideTokenRepository()
    {
        return new PostgresTokenRepository();
    }

    @Provides
    @ModuleScope
    public UserRepository provideUserRepository()
    {
        return new PostgresUserRepository();
    }
}
