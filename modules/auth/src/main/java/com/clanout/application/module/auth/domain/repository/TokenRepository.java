package com.clanout.application.module.auth.domain.repository;

public interface TokenRepository
{
    String getEncryptionSeed();

    void saveRefreshToken(String userId, String refreshToken);

    void updateRefreshToken(String oldToken, String newToken);

    String fetchUserIdFromRefreshToken(String refreshToken);
}
