package com.clanout.application.module.plan.data.plan;

import com.clanout.application.library.mongo.MongoDataSource;
import com.clanout.application.library.mongo.MongoDateTimeMapper;
import com.clanout.application.module.plan.domain.exception.FeedNotFoundException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Feed;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FeedRepositoryImpl implements FeedRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String MONGO_USER_FEED_COLLECTION = "user_feed";
    private static final String MONGO_PLAN_COLLECTION = "plans";
    private static final String MONGO_USER_ARCHIVE_COLLECTION = "user_archive";

    @Override
    public void initializeFeed(String userId)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> collection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            Document createObject = new Document();
            createObject.put("user_id", userId);
            createObject.put("updated_at", MongoDateTimeMapper.map(OffsetDateTime.now()));
            createObject.put("plans", new ArrayList<>());
            collection.insertOne(createObject);
        }
        catch (Exception e)
        {
            LOG.error("Unable to create feed [" + e.getMessage() + "]");
        }
    }

    @Override
    public void add(String userId, Plan plan)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> collection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true);

            Document planEntry = new Document();
            planEntry.put("plan_id", plan.getId());
            planEntry.put("rsvp", plan.getRsvp().name());
            planEntry.put("status", plan.getStatus());
            planEntry.put("friends", plan.getFriends());
            planEntry.put("inviter", plan.getInviter());
            planEntry.put("invitee", plan.getInvitee());

            /* Remove Plan Entry (if exists) */
            Document updateObject = new Document();
            updateObject.put("$pull", new Document("plans", new Document("plan_id", plan.getId())));
            collection.updateOne(new Document("user_id", userId), updateObject, updateOptions);

            /* Insert Plan Entry */
            updateObject = new Document();
            updateObject.put("$addToSet", new Document("plans", planEntry));
            collection.updateOne(new Document("user_id", userId), updateObject, updateOptions);
        }
        catch (Exception e)
        {
            LOG.error("Unable to add plan to feed [" + e.getMessage() + "]");
        }
    }

    @Override
    public void remove(String userId, String planId)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> collection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            Document updateObject = new Document();
            updateObject.put("$pull", new Document("plans", new Document("plan_id", planId)));
            collection.updateOne(new Document("user_id", userId), updateObject);
        }
        catch (Exception e)
        {
            LOG.error("Unable to remove plan from feed [" + e.getMessage() + "]");
        }
    }

    @Override
    public Plan fetch(String userId, String planId) throws PlanNotFoundException
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> feedCollection = database.getCollection(MONGO_USER_FEED_COLLECTION);
            MongoCollection<Document> planCollection = null;

            Document planContext = null;
            try
            {
                Document filteredFeed = feedCollection
                        .find(new Document("user_id", userId))
                        .projection(Projections.elemMatch("plans", new Document("plan_id", planId)))
                        .first();

                planContext = ((ArrayList<Document>) filteredFeed.get("plans")).get(0);
                if (planContext == null)
                {
                    throw new NullPointerException();
                }
            }
            catch (Exception e)
            {
                throw new PlanNotFoundException();
            }

            Document planDocument = null;
            try
            {
                planCollection = database.getCollection(MONGO_PLAN_COLLECTION);
                planDocument = planCollection.find(new Document("_id", new ObjectId(planId))).first();
                if (planDocument == null)
                {
                    throw new NullPointerException();
                }
            }
            catch (Exception e)
            {
                throw new PlanNotFoundException();
            }

            return MongoPlanMapper.map(planDocument, planContext);
        }
        catch (PlanNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            LOG.error("Unable to read plan [" + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public Feed fetchFeed(String userId, OffsetDateTime lastUpdated) throws FeedNotFoundException
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> feedCollection = database.getCollection(MONGO_USER_FEED_COLLECTION);
            MongoCollection<Document> planCollection = null;

            Document feedDocument = feedCollection
                    .find(new Document("user_id", userId))
                    .first();

            if (feedDocument == null)
            {
                throw new FeedNotFoundException();
            }

            Feed feed = new Feed();
            try
            {
                feed.setUpdatedAt(MongoDateTimeMapper.map(feedDocument, "updated_at"));
            }
            catch (Exception e)
            {
                feed.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                markFeedUpdated(userId);
            }

            if (lastUpdated != null && !feed.getUpdatedAt().isAfter(lastUpdated))
            {
                return feed;
            }

            planCollection = database.getCollection(MONGO_PLAN_COLLECTION);

            List<Plan> plans = new ArrayList<>();
            ArrayList<Document> planContextDocuments = (ArrayList<Document>) feedDocument.get("plans");
            for (Document planContextDocument : planContextDocuments)
            {
                String planId = planContextDocument.getString("plan_id");
                try
                {
                    Document planDocument = planCollection.find(new Document("_id", new ObjectId(planId))).first();
                    Plan plan = MongoPlanMapper.map(planDocument, planContextDocument);
                    plans.add(plan);
                }
                catch (Exception e)
                {
                    LOG.error("Unable to read feed entry " + planId + " [" + e.getMessage() + "]");
                }
            }
            feed.setPlans(plans);

            return feed;
        }
        catch (FeedNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error("Unable to read feed [" + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void markFeedUpdated(String userId)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> collection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            Document updateObject = new Document();
            updateObject.put("$set", new Document("updated_at", MongoDateTimeMapper.map(OffsetDateTime.now())));
            collection.updateOne(new Document("user_id", userId), updateObject);
        }
        catch (Exception e)
        {
            LOG.error("Unable to update feed updated_at time [" + e.getMessage() + "]");
        }
    }

    @Override
    public boolean updateRsvp(String userId, String planId, Rsvp rsvp)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> planCollection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            Document searchQuery = new Document();
            searchQuery.put("user_id", userId);
            searchQuery.put("plans.plan_id", planId);

            Document updateObject = new Document();
            updateObject.put("plans.$.rsvp", rsvp.name());
            UpdateResult updateResult = planCollection.updateOne(searchQuery, new Document("$set", updateObject));
            return updateResult.getModifiedCount() > 0;
        }
        catch (Exception e)
        {
            LOG.error("Unable to update rsvp [" + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void updateStatus(String userId, String planId, String status)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> planCollection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            Document searchQuery = new Document();
            searchQuery.put("user_id", userId);
            searchQuery.put("plans.plan_id", planId);

            Document updateObject = new Document();
            updateObject.put("plans.$.status", status);
            planCollection.updateOne(searchQuery, new Document("$set", updateObject));
        }
        catch (Exception e)
        {
            LOG.error("Unable to update status in feed [" + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void invite(String userId, String planId, List<String> to)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> planCollection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            Document searchQuery = new Document();
            searchQuery.put("user_id", userId);
            searchQuery.put("plans.plan_id", planId);

            Document updateObject = new Document();
            updateObject.put("plans.$.invitee", new Document("$each", to));
            planCollection.updateOne(searchQuery, new Document("$addToSet", updateObject));
        }
        catch (Exception e)
        {
            LOG.error("Unable to add invitee in feed [" + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public List<String> fetchPlanViewers(String planId)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> feedCollection = database.getCollection(MONGO_USER_FEED_COLLECTION);

            Document query = new Document();
            query.put("plans.plan_id", planId);

            List<String> userIds = new ArrayList<>();
            feedCollection
                    .find(query)
                    .forEach((Consumer<Document>) document -> {
                        userIds.add(document.getString("user_id"));
                    });
            return userIds;
        }
        catch (Exception e)
        {
            LOG.error("Unable to plan viewers [" + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }

    @Override
    public void archive(String planId, List<String> userIds)
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> userArchive = database.getCollection(MONGO_USER_ARCHIVE_COLLECTION);

            for (String userId : userIds)
            {
                try
                {
                    UpdateOptions updateOptions = new UpdateOptions();
                    updateOptions.upsert(true);

                    Document updateObject = new Document();
                    updateObject.put("$addToSet", new Document("plans", planId));
                    userArchive.updateOne(new Document("user_id", userId), updateObject, updateOptions);
                }
                catch (Exception e)
                {
                    LOG.error("Unable to add plan to user archive [" + e.getMessage() + "]");
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException();
        }
    }
}
