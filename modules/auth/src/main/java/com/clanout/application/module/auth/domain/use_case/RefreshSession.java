package com.clanout.application.module.auth.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.StringUtils;
import com.clanout.application.module.auth.domain.exception.InvalidRefreshTokenException;
import com.clanout.application.module.auth.domain.exception.RefreshSessionException;
import com.clanout.application.module.auth.domain.repository.TokenRepository;
import com.clanout.application.module.auth.domain.service.TokenService;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class RefreshSession
{
    private ExecutorService backgroundPool;

    private TokenRepository tokenRepository;
    private TokenService tokenService;

    @Inject
    public RefreshSession(ExecutorService backgroundPool, TokenRepository tokenRepository,
                          TokenService tokenService)
    {
        this.backgroundPool = backgroundPool;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    public Response execute(Request request) throws InvalidRefreshTokenException, RefreshSessionException
    {
        if (StringUtils.isNullOrEmpty(request.refreshToken))
        {
            throw new InvalidRefreshTokenException();
        }

        String userId = tokenRepository.fetchUserIdFromRefreshToken(request.refreshToken);
        if (userId == null)
        {
            throw new InvalidRefreshTokenException();
        }

        try
        {
            final String accessToken = tokenService.generateAccessToken(userId);
            final String refreshToken = tokenService.generateRefreshToken();

            backgroundPool.execute(() -> {
                tokenRepository.updateRefreshToken(request.refreshToken, refreshToken);
            });

            Response response = new Response();
            response.accessToken = accessToken;
            response.refreshToken = refreshToken;

            return response;
        }
        catch (Exception e)
        {
            throw new RefreshSessionException();
        }
    }

    public static class Request
    {
        public String refreshToken;
    }

    public static class Response
    {
        public String accessToken;
        public String refreshToken;
    }
}
