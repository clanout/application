package com.clanout.application.module.auth.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.auth.domain.exception.CreateSessionException;
import com.clanout.application.module.auth.domain.repository.TokenRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.security.Key;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

@ModuleScope
public class TokenService
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String ALGORITHM = "AES";

    private TokenRepository tokenRepository;

    private static Key TOKEN_ENCRYPTION_KEY;
    private static OffsetDateTime ENCRYPTION_KEY_LAST_UPDATE_TIMESTAMP;
    private static final int ENCRYPTION_KEY_UPDATE_INTERVAL = 3600;

    @Inject
    public TokenService(TokenRepository tokenRepository)
    {
        this.tokenRepository = tokenRepository;
    }

    public String generateAccessToken(String userId) throws CreateSessionException
    {
        try
        {
            StringBuilder checksum = new StringBuilder();
            checksum.append(userId);
            checksum.append("_");
            checksum.append(OffsetDateTime.now(ZoneOffset.UTC).toString());
            return encrypt(checksum.toString(), getKey());
        }
        catch (Exception e)
        {
            throw new CreateSessionException();
        }
    }

    public String generateRefreshToken()
    {
        return Base64.encodeBase64URLSafeString(UUID.randomUUID().toString().getBytes());
    }

    public String getUserId(String accessToken)
    {
        try
        {
            String checksum = decrypt(accessToken, getKey());
            String[] checksumTokens = checksum.split("_");
            return checksumTokens[0];
        }
        catch (Exception e)
        {
            LOG.info("Invalid access token [" + e.getMessage() + "]");
            return null;
        }
    }

    private String encrypt(final String valueEnc, Key key) throws Exception
    {
        try
        {
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            final byte[] encValue = c.doFinal(valueEnc.getBytes());
            return Base64.encodeBase64URLSafeString(encValue);
        }
        catch (Exception e)
        {
            LOG.error("Encryption Failed [" + e.getMessage() + "]");
            throw e;
        }
    }

    private String decrypt(final String encryptedValue, Key key) throws Exception
    {
        try
        {
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            final byte[] decorVal = Base64.decodeBase64(encryptedValue);
            final byte[] decValue = c.doFinal(decorVal);
            return new String(decValue);
        }
        catch (Exception e)
        {
            LOG.error("Decryption failed [" + e.getMessage() + "]");
            throw e;
        }
    }

    private Key getKey()
    {
        try
        {
            if (TOKEN_ENCRYPTION_KEY == null || isEncryptionKeyExpired())
            {
                String seed = tokenRepository.getEncryptionSeed();

                byte[] key = seed.getBytes("UTF-8");
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16); // use only first 128 bit

                TOKEN_ENCRYPTION_KEY = new SecretKeySpec(key, ALGORITHM);
                ENCRYPTION_KEY_LAST_UPDATE_TIMESTAMP = OffsetDateTime.now();
            }

            return TOKEN_ENCRYPTION_KEY;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private boolean isEncryptionKeyExpired()
    {
        OffsetDateTime now = OffsetDateTime.now();
        return ChronoUnit
                .SECONDS
                .between(ENCRYPTION_KEY_LAST_UPDATE_TIMESTAMP, now) > ENCRYPTION_KEY_UPDATE_INTERVAL;
    }
}
