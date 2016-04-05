package com.clanout.application.module.plan.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.module.plan.domain.exception.FeedNotFoundException;
import com.clanout.application.module.plan.domain.model.Feed;
import com.clanout.application.module.plan.domain.repository.FeedRepository;

import javax.inject.Inject;
import java.time.OffsetDateTime;

@ModuleScope
public class FetchFeed
{
    private FeedRepository feedRepository;

    @Inject
    public FetchFeed(FeedRepository feedRepository)
    {
        this.feedRepository = feedRepository;
    }

    public Response execute(Request request) throws FeedNotFoundException
    {
        Feed feed = feedRepository.fetch(request.userId, request.lastUpdated);

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
