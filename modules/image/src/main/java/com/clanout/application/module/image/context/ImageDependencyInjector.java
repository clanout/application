package com.clanout.application.module.image.context;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.image.domain.use_case.FetchProfileImage;
import dagger.Component;

@ModuleScope
@Component(modules = ImageDependencyProvider.class)
interface ImageDependencyInjector
{
    FetchProfileImage fetchProfileImage();
}
