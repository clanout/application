package com.clanout.application.module.auth.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.auth.domain.exception.CreateSessionException;
import com.clanout.application.module.auth.domain.exception.InvalidAuthMethodException;
import com.clanout.application.module.auth.domain.exception.InvalidAuthTokenException;
import com.clanout.application.module.auth.domain.model.AuthMethod;
import com.clanout.application.module.auth.domain.model.AuthenticatedUser;
import com.clanout.application.module.auth.domain.repository.TokenRepository;
import com.clanout.application.module.auth.domain.service.FacebookService;
import com.clanout.application.module.auth.domain.service.TokenService;
import com.clanout.application.module.user.domain.exception.InvalidUserFieldException;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class CreateSession
{
    private ExecutorService backgroundPool;

    private TokenService tokenService;
    private FacebookService facebookService;
    private TokenRepository tokenRepository;

    @Inject
    public CreateSession(ExecutorService backgroundPool, TokenService tokenService,
                         FacebookService facebookService, TokenRepository tokenRepository)
    {
        this.backgroundPool = backgroundPool;
        this.tokenService = tokenService;
        this.facebookService = facebookService;
        this.tokenRepository = tokenRepository;
    }

    public Response execute(Request request) throws InvalidAuthMethodException,
            InvalidAuthTokenException, CreateSessionException,
            InvalidUserFieldException, com.clanout.application.module.user.domain.exception.CreateUserException
    {
        AuthMethod authMethod = null;
        try
        {
            authMethod = AuthMethod.valueOf(request.authMethod);
        }
        catch (Exception e)
        {
            throw new InvalidAuthMethodException();
        }

        if (StringUtils.isNullOrEmpty(request.authToken))
        {
            throw new InvalidAuthTokenException();
        }

        AuthenticatedUser user = null;
        switch (authMethod)
        {
            case FACEBOOK:
                user = facebookService.getAUthenticatedUser(request.authToken);
                break;

            default:
                throw new InvalidAuthMethodException();
        }

        try
        {
            String accessToken = tokenService.generateAccessToken(user.getUserId());
            String refreshToken = tokenService.generateRefreshToken();

            final String userId = user.getUserId();
            backgroundPool.execute(() -> {
                tokenRepository.saveRefreshToken(userId, refreshToken);
            });


            Response response = new Response();
            response.accessToken = accessToken;
            response.refreshToken = refreshToken;
            response.isNewUser = user.isNew();

            return response;
        }
        catch (Exception e)
        {
            throw new CreateSessionException();
        }
    }

    public static class Request
    {
        public String authMethod;
        public String authToken;
    }

    public static class Response
    {
        public String accessToken;
        public String refreshToken;
        public boolean isNewUser;
    }
}
