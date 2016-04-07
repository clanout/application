package com.clanout.application.module.chat.data.chat;

import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.library.postgres.PostgresQuery;
import com.clanout.application.module.chat.domain.repository.ChatRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresChatRepository implements ChatRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String SQL_READ_USER_NAME = PostgresQuery.load("read_user_name.sql", PostgresChatRepository.class);

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
