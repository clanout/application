package com.clanout.application.module.auth.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.auth.domain.observer.AuthModuleObservers;
import com.clanout.application.module.auth.domain.observer.NewUserRegistrationObserver;
import com.clanout.application.module.auth.domain.use_case.CreateSession;
import com.clanout.application.module.auth.domain.use_case.RefreshSession;
import com.clanout.application.module.auth.domain.use_case.VerifySession;
import com.clanout.application.module.user.context.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private AuthDependencyInjector injector;

    private UserContext userContext;

    /* Observers */
    AuthModuleObservers observers;

    public AuthContext(UserContext userContext)
    {
        LOG.debug("[AuthContext initialized]");
        this.userContext = userContext;

        observers = new AuthModuleObservers();

        injector = DaggerAuthDependencyInjector
                .builder()
                .authDependencyProvider(new AuthDependencyProvider(this, userContext))
                .build();
    }

    @Override
    public void destroy()
    {
        injector = null;
        LOG.info("[AuthContext destroyed]");
    }

    public CreateSession createSession()
    {
        return injector.createSession();
    }

    public VerifySession verifySession()
    {
        return injector.verifySession();
    }

    public RefreshSession refreshSession()
    {
        return injector.refreshSession();
    }

    public void registerNewUserRegistrartionObserver(NewUserRegistrationObserver observer)
    {
        observers.registerNewUserRegistrationObserver(observer);
    }
}
