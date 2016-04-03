package com.clanout.application.module.user.data.postgres;

import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.library.postgres.PostgresQuery;
import com.clanout.application.module.user.domain.model.Friend;
import com.clanout.application.module.user.domain.model.User;
import com.clanout.application.module.user.domain.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgresUserRepository implements UserRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String SQL_INSERT_USER = PostgresQuery.load("insert_user.sql", PostgresUserRepository.class);
    private static final String SQL_INSERT_FRIENDS = PostgresQuery.load("insert_friends.sql", PostgresUserRepository.class);
    private static final String SQL_INSERT_BLOCKED_FRIENDS = PostgresQuery.load("insert_blocked_friends.sql", PostgresUserRepository.class);

    private static final String SQL_READ_USER = PostgresQuery.load("read_user.sql", PostgresUserRepository.class);
    private static final String SQL_READ_USER_USERNAME = PostgresQuery.load("read_user_username.sql", PostgresUserRepository.class);
    private static final String SQL_READ_USERS_USERNAME = PostgresQuery.load("read_users_username.sql", PostgresUserRepository.class);
    private static final String SQL_READ_FRIENDS = PostgresQuery.load("read_friends.sql", PostgresUserRepository.class);

    private static final String SQL_UPDATE_LOCATION = PostgresQuery.load("update_location.sql", PostgresUserRepository.class);
    private static final String SQL_UPDATE_MOBILE = PostgresQuery.load("update_mobile.sql", PostgresUserRepository.class);

    private static final String SQL_DELETE_FRIENDS = PostgresQuery.load("delete_friends.sql", PostgresUserRepository.class);
    private static final String SQL_DELETE_BLOCKED_FRIENDS = PostgresQuery.load("delete_blocked_friends.sql", PostgresUserRepository.class);

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
    public User fetch(String userId)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_USER))
        {
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                try
                {
                    User user = PostgresUserMapper.map(resultSet);
                    resultSet.close();
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

    @Override
    public User fetch(String usernameType, String username)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_USER_USERNAME))
        {
            statement.setString(1, usernameType);
            statement.setString(2, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                try
                {
                    User user = PostgresUserMapper.map(resultSet);
                    resultSet.close();
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

    @Override
    public List<String> fetch(String usernameType, List<String> usernames)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_USERS_USERNAME))
        {
            statement.setString(1, usernameType);
            statement.setArray(2, connection.createArrayOf("varchar", usernames.toArray()));

            List<String> userIds = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                userIds.add(resultSet.getString("user_id"));
            }
            resultSet.close();

            return userIds;
        }
        catch (SQLException e)
        {
            LOG.error("UserIDs Read Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            return new ArrayList<>();
        }
    }

    @Override
    public void addFriends(String userId, List<String> friends)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_FRIENDS))
        {
            statement.setString(1, userId);
            statement.setArray(2, connection.createArrayOf("varchar", friends.toArray()));

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            LOG.error("Add Friends Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public List<Friend> fetchFriends(String userId)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_FRIENDS))
        {
            statement.setString(1, userId);
            statement.setString(2, userId);
            statement.setString(3, userId);

            List<Friend> friends = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Friend friend = new Friend();
                friend.setUserId(resultSet.getString("friend_id"));
                friend.setName(resultSet.getString("name"));
                friend.setLocationZone(resultSet.getString("location_zone"));
                friend.setIsBlocked(resultSet.getBoolean("is_blocked"));

                friends.add(friend);
            }
            resultSet.close();

            return friends;
        }
        catch (SQLException e)
        {
            LOG.error("Friends Read Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateLocation(String userId, String locationZone)
    {
        User user = fetch(userId);
        if (user.getLocationZone().equalsIgnoreCase(locationZone))
        {
            return false;
        }
        else
        {
            try (Connection connection = PostgresDataSource.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_LOCATION))
            {
                statement.setString(1, locationZone);
                statement.setString(2, userId);

                statement.executeUpdate();
                return true;
            }
            catch (SQLException e)
            {
                LOG.error("Location Update Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void updateMobileNumber(String userId, String mobileNumber)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_MOBILE))
        {
            statement.setString(1, mobileNumber);
            statement.setString(2, userId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            LOG.error("Mobile Number Update Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void block(String userId, List<String> toBlock)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection())
        {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_BLOCKED_FRIENDS);
            statement.setString(1, userId);
            statement.setArray(2, connection.createArrayOf("varchar", toBlock.toArray()));
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(SQL_DELETE_FRIENDS);
            statement.setString(1, userId);
            statement.setArray(2, connection.createArrayOf("varchar", toBlock.toArray()));
            statement.setString(3, userId);
            statement.setArray(4, connection.createArrayOf("varchar", toBlock.toArray()));
            statement.executeUpdate();
            statement.close();

            connection.commit();
        }
        catch (SQLException e)
        {
            LOG.error("Block Friends Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void unblock(String userId, List<String> toUnblock)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection())
        {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_FRIENDS);
            statement.setString(1, userId);
            statement.setArray(2, connection.createArrayOf("varchar", toUnblock.toArray()));
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(SQL_DELETE_BLOCKED_FRIENDS);
            statement.setString(1, userId);
            statement.setArray(2, connection.createArrayOf("varchar", toUnblock.toArray()));
            statement.executeUpdate();
            statement.close();

            connection.commit();
        }
        catch (SQLException e)
        {
            LOG.error("Unblock Friends Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }
}
