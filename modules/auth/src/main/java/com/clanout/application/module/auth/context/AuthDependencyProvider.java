package com.clanout.application.module.auth.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.async.AsyncPool;
import com.clanout.application.module.auth.data.token.PostgresTokenRepository;
import com.clanout.application.module.auth.domain.observer.AuthModuleObservers;
import com.clanout.application.module.auth.domain.repository.TokenRepository;
import com.clanout.application.module.user.context.UserContext;
import com.clanout.application.module.user.domain.use_case.AddFriends;
import com.clanout.application.module.user.domain.use_case.CreateUser;
import com.clanout.application.module.user.domain.use_case.FetchUserFromUsername;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutorService;

@Module
class AuthDependencyProvider
{
    private AuthContext authContext;
    private UserContext userContext;

    public AuthDependencyProvider(AuthContext authContext, UserContext userContext)
    {
        this.authContext = authContext;
        this.userContext = userContext;
    }

    @Provides
    @ModuleScope
    public ExecutorService backgroundPool()
    {
        return AsyncPool.getInstance().getBackgroundPool();
    }

    @Provides
    @ModuleScope
    public AuthModuleObservers provideAuthModuleObservers()
    {
        return authContext.observers;
    }

    @Provides
    @ModuleScope
    public CreateUser provideCreateUser()
    {
        return userContext.createUser();
    }

    @Provides
    @ModuleScope
    public FetchUserFromUsername provideFetchUserFromUsername()
    {
        return userContext.fetchUserFromUsername();
    }

    @Provides
    @ModuleScope
    public AddFriends provideAddFriends()
    {
        return userContext.addFriends();
    }

    @Provides
    @ModuleScope
    public TokenRepository provideTokenRepository()
    {
        return new PostgresTokenRepository();
    }
}
