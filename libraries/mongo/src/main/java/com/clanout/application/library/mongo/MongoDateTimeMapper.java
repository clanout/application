package com.clanout.application.library.mongo;

import org.bson.BsonDateTime;
import org.bson.Document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class MongoDateTimeMapper
{
    public static OffsetDateTime map(Document document, String key)
    {
        long time = ((Date) document.get(key)).getTime();
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneOffset.UTC);
    }

    public static BsonDateTime map(OffsetDateTime offsetDateTime)
    {
        OffsetDateTime offsetDateTimeUtc = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
        return new BsonDateTime(offsetDateTimeUtc.toEpochSecond());
    }
}
