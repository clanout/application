package com.clanout.application.module.auth.data.postgres;

import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.library.postgres.PostgresQuery;
import com.clanout.application.module.auth.domain.model.User;
import com.clanout.application.module.auth.domain.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PostgresUserRepository implements UserRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String SQL_INSERT_USER = PostgresQuery.load("insert_user.sql", PostgresTokenRepository.class);
    private static final String SQL_READ_USER = PostgresQuery.load("read_user.sql", PostgresTokenRepository.class);

    @Override
    public void create(User user)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_USER))
        {
            statement.setString(1, user.getUserId());
            statement.setString(2, user.getFirstname());
            statement.setString(3, user.getLastname());

            if (user.getEmail() != null)
            {
                statement.setString(4, user.getEmail());
            }
            else
            {
                statement.setNull(4, Types.VARCHAR);
            }

            if (user.getMobileNumber() != null)
            {
                statement.setString(5, user.getMobileNumber());
            }
            else
            {
                statement.setNull(5, Types.VARCHAR);
            }

            statement.setString(6, user.getGender());
            statement.setString(7, user.getUsername());
            statement.setString(8, user.getUsernameType());
            statement.setString(9, user.getLocationZone());
            statement.setTimestamp(10, Timestamp.from(user.getCreatedAt().toInstant()));

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            LOG.error("Create User Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public User fetch(String usernameType, String username)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_USER))
        {
            statement.setString(1, usernameType);
            statement.setString(2, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                try
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
                catch (Exception e)
                {
                    resultSet.close();
                    throw new SQLException();
                }
            }
            else
            {
                return null;
            }
        }
        catch (SQLException e)
        {
            LOG.error("User Read Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            return null;
        }
    }
}
