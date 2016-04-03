package com.clanout.application.module.auth.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.auth.domain.use_case.CreateSession;
import com.clanout.application.module.auth.domain.use_case.RefreshSession;
import com.clanout.application.module.auth.domain.use_case.VerifySession;
import dagger.Component;

@ModuleScope
@Component(modules = AuthDependencyProvider.class)
interface AuthDependencyInjector
{
    CreateSession createSession();

    VerifySession verifySession();

    RefreshSession refreshSession();
}
