package com.clanout.application.module.image.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.image.data.image.PostgresCachedImageRepository;
import com.clanout.application.module.image.data.image.RedisCachedImageRepository;
import com.clanout.application.module.image.domain.repository.CachedImageRepository;
import com.clanout.application.module.image.domain.repository.PersistentImageRepository;
import com.clanout.application.module.user.context.UserContext;
import com.clanout.application.module.user.domain.use_case.FetchUser;
import dagger.Module;
import dagger.Provides;

@Module
class ImageDependencyProvider
{
    private UserContext userContext;

    public ImageDependencyProvider(UserContext userContext)
    {
        this.userContext = userContext;
    }

    @Provides
    @ModuleScope
    public FetchUser provideFetchUser()
    {
        return userContext.fetchUser();
    }

    @Provides
    @ModuleScope
    public CachedImageRepository provideCachedImageRepository()
    {
        return new RedisCachedImageRepository();
    }

    @Provides
    @ModuleScope
    public PersistentImageRepository providePersistentImageRepository()
    {
        return new PostgresCachedImageRepository(userContext.fetchUser());
    }
}
