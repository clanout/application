package com.clanout.application.module.image.context;

import com.clanout.application.framework.module.Context;
import com.clanout.application.module.image.domain.use_case.FetchProfileImage;
import com.clanout.application.module.user.context.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageContext implements Context
{
    private static Logger LOG = LogManager.getRootLogger();

    private ImageDependencyInjector injector;

    private UserContext userContext;

    public ImageContext(UserContext userContext)
    {
        LOG.debug("[ImageContext initialized]");
        this.userContext = userContext;
        injector = DaggerImageDependencyInjector
                .builder()
                .imageDependencyProvider(new ImageDependencyProvider(userContext))
                .build();
    }

    @Override
    public void destroy()
    {
        injector = null;
        LOG.info("[ImageContext destroyed]");
    }

    public FetchProfileImage fetchProfileImage()
    {
        return injector.fetchProfileImage();
    }
}
