package com.clanout.application.module.image.data.image;

import com.clanout.application.module.image.domain.repository.PersistentImageRepository;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.use_case.FetchUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresCachedImageRepository implements PersistentImageRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String USERNAME_TYPE_FACEBOOK = "FACEBOOK";
    private static final String URL_FACEBOOK_PROFILE_IMAGE = "https://graph.facebook.com/v2.4/$$$/picture";

    private FetchUser fetchUser;

    public PostgresCachedImageRepository(FetchUser fetchUser)
    {
        this.fetchUser = fetchUser;
    }

    @Override
    public String fetchProfileImageUrl(String userId)
    {
        try
        {
            User user = fetchUser(userId);
            if (user.getUsernameType().equalsIgnoreCase(USERNAME_TYPE_FACEBOOK))
            {
                return URL_FACEBOOK_PROFILE_IMAGE.replace("$$$", user.getUsername());
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            LOG.error("Profile Image Read Error [" + e.getMessage() + "]");
            return null;
        }
    }

    private User fetchUser(String userId) throws Exception
    {
        FetchUser.Request request = new FetchUser.Request();
        request.userId = userId;
        FetchUser.Response response = fetchUser.execute(request);
        return response.user;
    }
}
