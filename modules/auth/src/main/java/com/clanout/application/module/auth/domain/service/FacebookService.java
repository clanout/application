package com.clanout.application.module.auth.domain.service;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.auth.domain.exception.InvalidAuthTokenException;
import com.clanout.application.module.auth.domain.model.RegisteredUser;
import com.clanout.application.module.user.domain.exception.CreateUserException;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.use_case.AddFriends;
import com.clanout.application.module.user.domain.use_case.CreateUser;
import com.clanout.application.module.user.domain.use_case.FetchUserFromUsername;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ModuleScope
public class FacebookService
{
    public static final String FACEBOOK_USERNAME_TYPE = "FACEBOOK";

    private static Logger LOG = LogManager.getRootLogger();

    private static final String URL_USER_DATA = "https://graph.facebook.com/v2.5/me" +
            "?fields=id,first_name,last_name,email,gender" +
            "&access_token=";

    private static final String URL_USER_FRIENDS = "https://graph.facebook.com/v2.5/me/friends?limit=5000&offset=0" +
            "&access_token=";

    private OkHttpClient okHttpClient;
    private FetchUserFromUsername fetchUserFromUsername;
    private CreateUser createUser;
    private AddFriends addFriends;

    @Inject
    public FacebookService(FetchUserFromUsername fetchUserFromUsername, CreateUser createUser, AddFriends addFriends)
    {
        okHttpClient = new OkHttpClient();
        this.fetchUserFromUsername = fetchUserFromUsername;
        this.createUser = createUser;
        this.addFriends = addFriends;
    }

    public RegisteredUser getRegisteredUser(String accessToken)
            throws InvalidAuthTokenException, InvalidFieldException, CreateUserException
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
            /* Create New User */
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

        /* Add Facebook/Refresh Friends */
        UserFriends userFriends = getFacebookFriends(accessToken);
        List<String> usernames = userFriends.friends
                .stream()
                .map(friend -> friend.id)
                .collect(Collectors.toList());

        AddFriends.Request addFriendsRequest = new AddFriends.Request();
        addFriendsRequest.userId = user.getUserId();
        addFriendsRequest.friendUsernameType = FACEBOOK_USERNAME_TYPE;
        addFriendsRequest.usernames = usernames;

        addFriends.execute(addFriendsRequest);

        return new RegisteredUser(user, isNew);
    }

    private UserFriends getFacebookFriends(String accessToken)
    {
        Response response = null;
        try
        {
            Request request = new Request.Builder()
                    .url(URL_USER_FRIENDS + accessToken)
                    .build();

            response = okHttpClient.newCall(request).execute();
            if (response.code() == 200)
            {
                return GsonProvider.getGson().fromJson(response.body().string(), UserFriends.class);
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
                LOG.error("Unable to fetch facebook friends [" + response.code() + " : " + response.body().string() + "]");
            }
            catch (Exception e1)
            {
                LOG.error("Unable to fetch facebook friends");
            }

            return null;
        }
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

    private static class UserData
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

    public static class UserFriends
    {
        @SerializedName("data")
        public List<Friend> friends;

        private static class Friend
        {
            @SerializedName("id")
            public String id;

            @SerializedName("name")
            public String name;
        }
    }
}
