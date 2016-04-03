package com.clanout.application.module.auth.data.postgres;

import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.library.postgres.PostgresQuery;
import com.clanout.application.module.auth.domain.repository.TokenRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class PostgresTokenRepository implements TokenRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String SQL_READ_SESSION_ENCRYPTION_KEY = PostgresQuery.load("read_session_encryption_key.sql", PostgresTokenRepository.class);
    private static final String SQL_READ_REFRESH_TOKEN = PostgresQuery.load("read_refresh_token.sql", PostgresTokenRepository.class);
    private static final String SQL_INSERT_REFRESH_TOKEN = PostgresQuery.load("insert_refresh_token.sql", PostgresTokenRepository.class);
    private static final String SQL_UPDATE_REFRESH_TOKEN = PostgresQuery.load("update_refresh_token.sql", PostgresTokenRepository.class);

    @Override
    public String getEncryptionSeed()
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_SESSION_ENCRYPTION_KEY))
        {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                String key = resultSet.getString("encryption_key");
                resultSet.close();
                return key;
            }
            else
            {
                return null;
            }
        }
        catch (SQLException e)
        {
            LOG.error("Encryption Key Read Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void saveRefreshToken(String userId, String refreshToken)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_REFRESH_TOKEN))
        {
            statement.setString(1, refreshToken);
            statement.setString(2, userId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            LOG.error("Create Refresh Token Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void updateRefreshToken(String oldToken, String newToken)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_REFRESH_TOKEN))
        {
            statement.setString(1, newToken);
            statement.setString(2, oldToken);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            LOG.error("Update Refresh Token Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public String fetchUserIdFromRefreshToken(String refreshToken)
    {
        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_REFRESH_TOKEN))
        {
            statement.setString(1, refreshToken);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                String key = resultSet.getString("user_id");
                resultSet.close();
                return key;
            }
            else
            {
                return null;
            }
        }
        catch (SQLException e)
        {
            LOG.error("Refresh Token Read Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }
}
