package com.clanout.application.module.auth.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.auth.domain.exception.InvalidAuthTokenException;
import com.clanout.application.module.auth.domain.model.AuthenticatedUser;
import com.clanout.application.module.user.domain.exception.CreateUserException;
import com.clanout.application.module.user.domain.exception.InvalidUserFieldException;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.use_case.CreateUser;
import com.clanout.application.module.user.domain.use_case.FetchUserFromUsername;
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
    private FetchUserFromUsername fetchUserFromUsername;
    private CreateUser createUser;

    @Inject
    public FacebookService(FetchUserFromUsername fetchUserFromUsername, CreateUser createUser)
    {
        okHttpClient = new OkHttpClient();
        this.fetchUserFromUsername = fetchUserFromUsername;
        this.createUser = createUser;
    }

    public AuthenticatedUser getAUthenticatedUser(String accessToken)
            throws InvalidAuthTokenException, InvalidUserFieldException, CreateUserException
    {
        UserData userData = getFacebookUserData(accessToken);
        if (userData == null)
        {
            throw new InvalidAuthTokenException();
        }

        FetchUserFromUsername.Request fetchUserRequest = new FetchUserFromUsername.Request();
        fetchUserRequest.usernameType = FACEBOOK_USERNAME_TYPE;
        fetchUserRequest.username = userData.id;
        FetchUserFromUsername.Response fetchUserResponse = fetchUserFromUsername.execute(fetchUserRequest);

        User user = fetchUserResponse.user;
        boolean isNew = false;

        if (user == null)
        {
            /* New User */
            CreateUser.Request request = new CreateUser.Request();
            request.firstname = userData.firstname;
            request.lastname = userData.lastname;
            request.email = userData.email;
            request.gender = userData.gender;
            request.usernameType = FACEBOOK_USERNAME_TYPE;
            request.username = userData.id;

            CreateUser.Response response = createUser.execute(request);
            user = response.user;
            isNew = true;
        }

        return new AuthenticatedUser(user, isNew);
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
