package com.clanout.application.module.plan.data.plan;

import com.clanout.application.library.mongo.MongoDataSource;
import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.plan.domain.exception.FeedNotFoundException;
import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.*;
import com.clanout.application.module.plan.domain.repository.FeedRepository;
import com.clanout.application.module.plan.domain.repository.PlanRepository;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

public class Test
{
    private static final String ADITYA = "9276fdbb-df34-44a6-93b4-f26147738227";
    private static final String HARSH = "b08f854b-91ef-4871-8cc4-233c13dd4504";

    static
    {
        GsonProvider.init();
    }

    private static final Gson GSON = GsonProvider
            .getGsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public static void main(String[] args) throws Exception
    {
        PostgresDataSource.getInstance().init();
        MongoDataSource.getInstance().init();

//        update();
        feed();
//        createPlans();
//        updateRsvp();


        MongoDataSource.getInstance().close();
        PostgresDataSource.getInstance().close();
    }

    public static void updateRsvp() throws PlanNotFoundException
    {
        String planId = "5703daab156407669fa8f976";

        PlanRepository planRepository = new PlanRepositoryImpl();
        FeedRepository feedRepository = new FeedRepositoryImpl();

        feedRepository.updateRsvp(ADITYA, planId, Rsvp.YES);
    }

    public static void update() throws PlanNotFoundException
    {
        String planId = "5703a9c4156407601a4260e6";

        PlanRepository planRepository = new PlanRepositoryImpl();
        FeedRepository feedRepository = new FeedRepositoryImpl();

        Location location = new Location();
        location.setName("Biere Club");
        location.setLatitude(12.971077);
        location.setLongitude(77.597533);

        System.out.println(GSON.toJson(feedRepository.fetch(ADITYA, planId)));
        planRepository.update(planId, "This is the first mongo plan", null, null, location);
        System.out.println(GSON.toJson(feedRepository.fetch(ADITYA, planId)));
    }

    public static void feed() throws FeedNotFoundException
    {
        FeedRepository feedRepository = new FeedRepositoryImpl();

        OffsetDateTime lastUpdated = OffsetDateTime.now().minusDays(10);
        Feed feed = feedRepository.fetch(HARSH, lastUpdated);
        System.out.println(GSON.toJson(feed));
    }

    public static void createPlans()
    {
        PlanRepository planRepository = new PlanRepositoryImpl();
        FeedRepository feedRepository = new FeedRepositoryImpl();

        Plan plan1 = getPlan1();
        Plan plan2 = getPlan2();
        plan1 = planRepository.create(plan1);
        plan2 = planRepository.create(plan2);

        feedRepository.add(ADITYA, plan1);
        feedRepository.add(ADITYA, plan2);
        feedRepository.markFeedUpdated(ADITYA);

        System.out.println(GSON.toJson(plan1));
        System.out.println(GSON.toJson(plan2));
    }

    public static Plan getPlan1()
    {
        Plan plan = new Plan();

        plan.setTitle("Plan Uno");
        plan.setType(Type.SECRET);
        plan.setCategory("DRINKS");
        plan.setCreatorId(ADITYA);

        plan.setVisibilityZones(Arrays.asList("IN_BLR"));

        plan.setDescription("This is the first mongo plan");
        plan.setStartTime(OffsetDateTime.now(ZoneOffset.UTC).plusHours(5));
        plan.setEndTime(plan.getStartTime().plusHours(7));

        Location location = new Location();
        location.setName("Biere Club");
        location.setLatitude(12.971077);
        location.setLongitude(77.597533);
        plan.setLocation(location);

        Attendee creator = new Attendee();
        creator.setId(ADITYA);
        creator.setName("Aditya Prasad");
        creator.setStatus("");
        plan.setAttendees(Arrays.asList(creator));

        plan.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        plan.setUpdatedAt(plan.getCreatedAt());

        plan.setRsvp(Rsvp.YES);
        plan.setStatus("Hello, World!");
        plan.setFriends(Arrays.asList(HARSH));
        plan.setInviter(new ArrayList<>());
        plan.setInvitee(Arrays.asList(HARSH));

        return plan;
    }

    public static Plan getPlan2()
    {
        Plan plan = new Plan();

        plan.setTitle("Plan Duo");
        plan.setType(Type.OPEN);
        plan.setCategory("EAT_OUT");
        plan.setCreatorId(HARSH);

        plan.setVisibilityZones(Arrays.asList("IN_BLR"));

        plan.setDescription("This is the second mongo plan");
        plan.setStartTime(OffsetDateTime.now(ZoneOffset.UTC).plusHours(5));
        plan.setEndTime(plan.getStartTime().plusHours(7));

        Attendee creator = new Attendee();
        creator.setId(HARSH);
        creator.setName("Harsh Pokharna");
        creator.setStatus("");
        plan.setAttendees(Arrays.asList(creator));

        plan.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        plan.setUpdatedAt(plan.getCreatedAt());

        plan.setRsvp(Rsvp.YES);
        plan.setStatus("Hello, World Again!");
        plan.setFriends(Arrays.asList(HARSH));
        plan.setInviter(Arrays.asList(HARSH));
        plan.setInvitee(new ArrayList<>());

        return plan;
    }
}
