package com.clanout.application.module.image.domain.repository;

public interface CachedImageRepository
{
    String fetchProfileImageUrl(String userId);

    void saveProfileImageUrl(String userId, String profileImageUrl);
}
