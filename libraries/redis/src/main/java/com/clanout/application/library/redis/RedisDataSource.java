package com.clanout.application.library.redis;

import com.clanout.application.framework.conf.Conf;
import com.clanout.application.framework.conf.ConfLoader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.SQLException;

public class RedisDataSource
{
    private static RedisDataSource instance;

    private JedisPool connectionPool;

    public static RedisDataSource getInstance()
    {
        if (instance == null)
        {
            instance = new RedisDataSource();
        }

        return instance;
    }

    private RedisDataSource()
    {
    }

    public void init() throws Exception
    {
        Conf dbConf = ConfLoader.getConf(RedisConf.DB);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(Integer.parseInt(dbConf.get("redis.pool.min_size")));
        poolConfig.setMaxTotal(Integer.parseInt(dbConf.get("redis.pool.max_size")));

        String host = dbConf.get("redis.host");
        int port = Integer.parseInt(dbConf.get("redis.port"));

        connectionPool = new JedisPool(poolConfig, host, port);
    }

    public Jedis getConnection()
    {
        return connectionPool.getResource();
    }

    public void close()
    {
        connectionPool.close();
    }
}
