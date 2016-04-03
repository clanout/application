package com.clanout.application.module.auth.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.auth.domain.exception.CreateUserException;
import com.clanout.application.module.auth.domain.exception.InvalidAuthTokenException;
import com.clanout.application.module.auth.domain.model.User;
import com.clanout.application.module.auth.domain.repository.UserRepository;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

@ModuleScope
public class FacebookService
{
    public static final String FACEBOOK_USERNAME_TYPE = "FACEBOOK";

    private static Logger LOG = LogManager.getRootLogger();

    private static final String URL_USER_DATA = "https://graph.facebook.com/v2.5/me" +
            "?fields=id,first_name,last_name,email,gender" +
            "&access_token=";

    private OkHttpClient okHttpClient;
    private UserRepository userRepository;
    private UserService userService;

    @Inject
    public FacebookService(UserRepository userRepository, UserService userService)
    {
        okHttpClient = new OkHttpClient();
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public User getUser(String accessToken) throws InvalidAuthTokenException, CreateUserException
    {
        UserData userData = getFacebookUserData(accessToken);
        if (userData == null)
        {
            throw new InvalidAuthTokenException();
        }

        User user = userRepository.fetch(FACEBOOK_USERNAME_TYPE, userData.id);
        if (user == null)
        {
            /* New User */
            user = new User();
            user.setFirstname(userData.firstname);
            user.setLastname(userData.lastname);
            user.setEmail(userData.email);
            user.setGender(userData.gender);
            user.setUsernameType(FACEBOOK_USERNAME_TYPE);
            user.setUsername(userData.id);

            user = userService.register(user);
        }

        return user;
    }

    private UserData getFacebookUserData(String accessToken)
    {
        Response response = null;
        try
        {
            Request request = new Request.Builder()
                    .url(URL_USER_DATA + accessToken)
                    .build();

            response = okHttpClient.newCall(request).execute();
            if (response.code() == 200)
            {
                return GsonProvider.getGson().fromJson(response.body().string(), UserData.class);
            }
            else
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            try
            {
                LOG.error("Unable to fetch facebook user data [" + response.code() + " : " + response.body().string() + "]");
            }
            catch (Exception e1)
            {
                LOG.error("Unable to fetch facebook user data");
            }

            return null;
        }
    }

    public static class UserData
    {
        @SerializedName("id")
        public String id;

        @SerializedName("first_name")
        public String firstname;

        @SerializedName("last_name")
        public String lastname;

        @SerializedName("email")
        public String email;

        @SerializedName("gender")
        public String gender;
    }
}
