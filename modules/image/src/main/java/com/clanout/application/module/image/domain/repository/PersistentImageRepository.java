package com.clanout.application.module.image.domain.repository;

public interface PersistentImageRepository
{
    String fetchProfileImageUrl(String userId);
}
