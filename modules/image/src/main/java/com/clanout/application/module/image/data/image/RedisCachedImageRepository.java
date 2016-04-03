package com.clanout.application.module.image.data.image;

import com.clanout.application.library.redis.RedisDataSource;
import com.clanout.application.module.image.domain.repository.CachedImageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

public class RedisCachedImageRepository implements CachedImageRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String PROFILE_IMAGE_KEY_PREFIX = "profile_image:";

    @Override
    public String fetchProfileImageUrl(String userId)
    {
        try (Jedis redis = RedisDataSource.getInstance().getConnection())
        {
            return redis.get(PROFILE_IMAGE_KEY_PREFIX + userId);
        }
        catch (Exception e)
        {
            LOG.error("Profile Image Read Error [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void saveProfileImageUrl(String userId, String profileImageUrl)
    {
        try (Jedis redis = RedisDataSource.getInstance().getConnection())
        {
            redis.set(PROFILE_IMAGE_KEY_PREFIX + userId, profileImageUrl);
        }
        catch (Exception e)
        {
            LOG.error("Profile Image Save Error [" + e.getMessage() + "]");
        }
    }
}
