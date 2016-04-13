package com.clanout.application.library.mongo;

import com.clanout.application.framework.conf.Conf;
import com.clanout.application.framework.conf.ConfLoader;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MongoDataSource
{
    private static Logger LOG = LogManager.getRootLogger();

    private static MongoDataSource instance;

    private MongoClient mongoClient;
    private static String MONGO_URL = "mongodb://";
    private static String DATABASE_NAME;

    public static MongoDataSource getInstance()
    {
        if (instance == null)
        {
            instance = new MongoDataSource();
        }

        return instance;
    }

    private MongoDataSource()
    {
    }

    public void init() throws Exception
    {
        Conf dbConf = ConfLoader.getConf(MongoConf.DB);
        String host = dbConf.get("mongo.host");
        int port = Integer.parseInt(dbConf.get("mongo.port"));
        int maxPoolSize = Integer.parseInt(dbConf.get("mongo.pool.max_size"));

        MONGO_URL = MONGO_URL + host + ":" + port + "/?maxPoolSize=" + maxPoolSize;
        DATABASE_NAME = dbConf.get("mongo.database");

        mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
    }

    public MongoDatabase getDatabase() throws Exception
    {
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    public void close()
    {
        mongoClient.close();
    }
}
