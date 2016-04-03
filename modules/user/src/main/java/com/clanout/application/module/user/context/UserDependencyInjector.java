package com.clanout.application.module.user.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.user.domain.use_case.CreateUser;
import com.clanout.application.module.user.domain.use_case.FetchUserFromUsername;
import dagger.Component;

@ModuleScope
@Component(modules = UserDependencyProvider.class)
interface UserDependencyInjector
{
    CreateUser createUser();

    FetchUserFromUsername fetchUserFromUsername();
}
