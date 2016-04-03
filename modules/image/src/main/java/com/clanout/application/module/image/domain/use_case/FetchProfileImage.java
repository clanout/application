package com.clanout.application.module.image.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.image.domain.exception.NoProfileImageException;
import com.clanout.application.module.image.domain.repository.CachedImageRepository;
import com.clanout.application.module.image.domain.repository.PersistentImageRepository;

import javax.inject.Inject;

@ModuleScope
public class FetchProfileImage
{
    private CachedImageRepository cachedImageRepository;
    private PersistentImageRepository persistentImageRepository;

    @Inject
    public FetchProfileImage(CachedImageRepository cachedImageRepository, PersistentImageRepository persistentImageRepository)
    {
        this.cachedImageRepository = cachedImageRepository;
        this.persistentImageRepository = persistentImageRepository;
    }

    public Response execute(Request request) throws NoProfileImageException
    {
        String profileImageUrl = cachedImageRepository.fetchProfileImageUrl(request.userId);
        if (profileImageUrl == null)
        {
            profileImageUrl = persistentImageRepository.fetchProfileImageUrl(request.userId);
            if (profileImageUrl != null)
            {
                cachedImageRepository.saveProfileImageUrl(request.userId, profileImageUrl);
            }
            else
            {
                throw new NoProfileImageException();
            }
        }

        Response response = new Response();
        response.profileImageUrl = profileImageUrl;
        return response;
    }

    public static class Request
    {
        public String userId;
    }

    public static class Response
    {
        public String profileImageUrl;
    }
}
