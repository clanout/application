package com.clanout.application.module.auth.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.auth.domain.service.TokenService;

import javax.inject.Inject;

@ModuleScope
public class VerifySession
{
    private TokenService tokenService;

    @Inject
    public VerifySession(TokenService tokenService)
    {
        this.tokenService = tokenService;
    }

    public Response execute(Request request)
    {
        String userId = tokenService.getUserId(request.accessToken);

        Response response = new Response();
        response.userId = userId;

        return response;
    }

    public static class Request
    {
        public String accessToken;
    }

    public static class Response
    {
        public String userId;
    }
}
