package com.clanout.application.module.user.data.postgres;

import com.clanout.application.module.user.domain.model.User;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PostgresUserMapper
{
    public static User map(ResultSet resultSet) throws Exception
    {
        User user = new User();
        user.setUserId(resultSet.getString("user_id"));
        user.setFirstname(resultSet.getString("first_name"));
        user.setLastname(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setMobileNumber(resultSet.getString("mobile_number"));
        user.setGender(resultSet.getString("gender"));
        user.setUsername(resultSet.getString("username"));
        user.setUsernameType(resultSet.getString("username_type"));
        user.setLocationZone(resultSet.getString("location_zone"));

        Timestamp createdAtTimestamp = resultSet.getTimestamp("created_at");
        OffsetDateTime createdAt = OffsetDateTime.ofInstant(createdAtTimestamp.toInstant(), ZoneOffset.UTC);
        user.setCreatedAt(createdAt);

        return user;
    }
}
