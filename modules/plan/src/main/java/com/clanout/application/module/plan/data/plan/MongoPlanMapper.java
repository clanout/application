package com.clanout.application.module.plan.data.plan;

import com.clanout.application.library.mongo.MongoDateTimeMapper;
import com.clanout.application.module.plan.domain.model.*;
import org.bson.Document;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class MongoPlanMapper
{
    public static Plan map(Document planDocument, Document planContext) throws Exception
    {
        Plan plan = new Plan();

        if (planDocument == null)
        {
            throw new NullPointerException();
        }

        plan.setId(planDocument.getObjectId("_id").toHexString());
        plan.setTitle(planDocument.getString("title"));
        plan.setType(Type.valueOf(planDocument.getString("type")));
        plan.setCategory(planDocument.getString("category"));
        plan.setCreatorId(planDocument.getString("creator_id"));

        plan.setVisibilityZones((ArrayList<String>) planDocument.get("visibility_zones"));

        plan.setDescription(planDocument.getString("description"));

        plan.setStartTime(MongoDateTimeMapper.map(planDocument, "start_time"));
        plan.setEndTime(MongoDateTimeMapper.map(planDocument, "end_time"));

        Document locationDocument = (Document) planDocument.get("location");
        if (locationDocument != null)
        {
            Location location = new Location();
            location.setName(locationDocument.getString("name"));
            location.setLatitude(locationDocument.getDouble("latitude"));
            location.setLongitude(locationDocument.getDouble("longitude"));
            plan.setLocation(location);
        }

        plan.setCreatedAt(MongoDateTimeMapper.map(planDocument, "created_at"));
        plan.setUpdatedAt(MongoDateTimeMapper.map(planDocument, "updated_at"));

        List<Attendee> attendees = new ArrayList<>();
        ArrayList<Document> attendeeDocuments = (ArrayList<Document>) planDocument.get("attendees");
        for (Document attendeeDocument : attendeeDocuments)
        {
            Attendee attendee = new Attendee();
            attendee.setId(attendeeDocument.getString("id"));
            attendee.setName(attendeeDocument.getString("name"));
            attendee.setStatus(attendeeDocument.getString("status"));
            attendees.add(attendee);
        }
        plan.setAttendees(attendees);

        if (planContext != null)
        {
            plan.setRsvp(Rsvp.valueOf(planContext.getString("rsvp")));
            plan.setStatus(planContext.getString("status"));
            plan.setFriends((ArrayList<String>) planContext.get("friends"));
            plan.setInviter((ArrayList<String>) planContext.get("inviter"));
            plan.setInvitee((ArrayList<String>) planContext.get("invitee"));
        }

        return plan;
    }
}
