package com.clanout.application.module.plan.data.plan;

import com.clanout.application.library.mongo.MongoDataSource;
import com.clanout.application.library.mongo.MongoDateTimeMapper;
import com.clanout.application.module.plan.domain.exception.FeedNotFoundException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Feed;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.Rsvp;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedRepositoryImpl implements FeedRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String MONGO_USER_FEED_COLLECTION = "user_feed";
    private static final String MONGO_PLAN_COLLECTION = "plans";

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
            collection.updateOne(new BasicDBObject("user_id", userId), updateObject, updateOptions);

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
            collection.updateOne(new BasicDBObject("user_id", userId), updateObject);
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
    public Feed fetch(String userId, OffsetDateTime lastUpdated) throws FeedNotFoundException
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
            feed.setUpdatedAt(MongoDateTimeMapper.map(feedDocument, "updated_at"));

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
            collection.updateOne(new BasicDBObject("user_id", userId), updateObject);
        }
        catch (Exception e)
        {
            LOG.error("Unable to update feed updated_at time [" + e.getMessage() + "]");
        }
    }

    @Override
    public boolean updateRsvp(String userId, String planId, Rsvp rsvp) throws PlanNotFoundException
    {
        try
        {
            MongoDatabase database = MongoDataSource.getInstance().getDatabase();
            MongoCollection<Document> feedCollection = database.getCollection(MONGO_USER_FEED_COLLECTION);

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

            String oldRsvp = planContext.getString("rsvp");
            if (oldRsvp.equals(rsvp.name()))
            {
                return false;
            }

            planContext.put("rsvp", rsvp.name());
            planContext.put("status", "");

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true);

            /* Remove Plan Entry (if exists) */
            Document updateObject = new Document();
            updateObject.put("$pull", new Document("plans", new Document("plan_id", planId)));
            feedCollection.updateOne(new BasicDBObject("user_id", userId), updateObject, updateOptions);

            /* Insert Plan Entry */
            updateObject = new Document();
            updateObject.put("$addToSet", new Document("plans", planContext));
            feedCollection.updateOne(new Document("user_id", userId), updateObject, updateOptions);

            return true;
        }
        catch (PlanNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            LOG.error("Unable to update rsvp [" + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }
}
