package com.clanout.application.library.postgres;

import com.clanout.application.framework.conf.Conf;
import com.clanout.application.framework.conf.ConfLoader;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresDataSource
{
    private static PostgresDataSource instance;

    private ComboPooledDataSource connectionPool;

    public static PostgresDataSource getInstance()
    {
        if (instance == null)
        {
            instance = new PostgresDataSource();
        }

        return instance;
    }

    private PostgresDataSource()
    {
    }

    public void init() throws Exception
    {
        Conf dbConf = ConfLoader.getConf(PostgresConf.DB);

        connectionPool = new ComboPooledDataSource();
        connectionPool.setDriverClass(dbConf.get("db.postgres.driver"));
        connectionPool.setJdbcUrl(dbConf.get("db.postgres.url"));
        connectionPool.setUser(dbConf.get("db.postgres.user"));
        connectionPool.setPassword(dbConf.get("db.postgres.password"));

        connectionPool.setMinPoolSize(Integer.parseInt(dbConf.get("db.postgres.pool.min_size")));
        connectionPool.setAcquireIncrement(Integer.parseInt(dbConf.get("db.postgres.pool.increment_size")));
        connectionPool.setMaxPoolSize(Integer.parseInt(dbConf.get("db.postgres.pool.max_size")));
    }

    public Connection getConnection() throws SQLException
    {
        return connectionPool.getConnection();
    }

    public void close()
    {
        connectionPool.close();
    }
}
