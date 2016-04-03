package com.clanout.application.module.user.domain;

import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.module.user.data.postgres.PostgresUserRepository;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.repository.UserRepository;

import java.util.Arrays;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        PostgresDataSource.getInstance().init();

        UserRepository userRepository = new PostgresUserRepository();
        User aditya = userRepository.fetch("9276fdbb-df34-44a6-93b4-f26147738227");
        User harsh = userRepository.fetch("b08f854b-91ef-4871-8cc4-233c13dd4504");

//        userRepository.block(aditya.getUserId(), Arrays.asList(harsh.getUserId()));
        userRepository.unblock(aditya.getUserId(), Arrays.asList(harsh.getUserId()));

        PostgresDataSource.getInstance().close();
    }
}
