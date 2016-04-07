package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.framework.module.InvalidFieldException;
import com.clanout.application.module.plan.domain.exception.FeedNotFoundException;
import com.clanout.application.module.plan.domain.model.Feed;
import com.clanout.application.module.plan.domain.repository.FeedRepository;

import javax.inject.Inject;
import java.time.OffsetDateTime;

@ModuleScope
public class FetchFeed
{
    private FeedRepository feedRepository;
    private RecalculateFeed recalculateFeed;

    @Inject
    public FetchFeed(FeedRepository feedRepository, RecalculateFeed recalculateFeed)
    {
        this.feedRepository = feedRepository;
        this.recalculateFeed = recalculateFeed;
    }

    public Response execute(Request request) throws InvalidFieldException
    {
        Feed feed = null;
        try
        {
            feed = feedRepository.fetchFeed(request.userId, request.lastUpdated);
        }
        catch (FeedNotFoundException e)
        {
            RecalculateFeed.Request recalculateFeedRequest = new RecalculateFeed.Request();
            recalculateFeedRequest.userId = request.userId;
            feed = recalculateFeed.execute(recalculateFeedRequest).feed;
        }

        Response response = new Response();
        if (feed.getPlans() == null)
        {
            response.isUpdated = false;
        }
        else
        {
            response.isUpdated = true;
            response.feed = feed;
        }

        return response;
    }

    public static class Request
    {
        public String userId;
        public OffsetDateTime lastUpdated;
    }

    public static class Response
    {
        public boolean isUpdated;
        public Feed feed;
    }
}
