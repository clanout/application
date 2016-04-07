package com.clanout.application.module.notification.data.gcm;

import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.library.postgres.PostgresQuery;
import com.clanout.application.module.notification.domain.repository.NotificationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresNotificationRepository implements NotificationRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String SQL_INSERT_GCM_TOKEN = PostgresQuery.load("insert_gcm_token.sql", PostgresNotificationRepository.class);
    private static final String SQL_READ_TOKENS = PostgresQuery.load("read_tokens.sql", PostgresNotificationRepository.class);
    private static final String SQL_READ_USER_NAME = PostgresQuery.load("read_user_name.sql", PostgresNotificationRepository.class);

    @Override
    public void register(String userId, String gcmToken)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_GCM_TOKEN))
        {
            statement.setString(1, userId);
            statement.setString(2, gcmToken);
            statement.setString(3, userId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            LOG.error("Gcm Token Insert Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public List<String> fetchTokens(List<String> userIds)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_TOKENS))
        {
            statement.setArray(1, connection.createArrayOf("varchar", userIds.toArray()));
            ResultSet resultSet = statement.executeQuery();

            List<String> gcmTokens = new ArrayList<>();
            while (resultSet.next())
            {
                userIds.add(resultSet.getString("gcm_token"));
            }
            resultSet.close();
            return gcmTokens;
        }
        catch (SQLException e)
        {
            LOG.error("Unable to read gcm_token [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public String getUserName(String userId)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_USER_NAME))
        {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();

            List<String> gcmTokens = new ArrayList<>();
            if (resultSet.next())
            {
                String name = resultSet.getString("name");
                resultSet.close();
                return name;
            }
            else
            {
                resultSet.close();
                return null;
            }
        }
        catch (SQLException e)
        {
            LOG.error("Unable to read user name [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }
}
