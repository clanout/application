package com.clanout.application.library.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public class OffsetDateTimeParser
{
    public static class Serializer implements JsonSerializer<OffsetDateTime>
    {
        @Override
        public JsonElement serialize(OffsetDateTime offsetDateTime, Type type, JsonSerializationContext jsonSerializationContext)
        {
            return new JsonPrimitive(offsetDateTime.toString());
        }
    }

    public static class Deserializer implements JsonDeserializer<OffsetDateTime>
    {
        @Override
        public OffsetDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
        {
            return OffsetDateTime.parse(jsonElement.getAsString());
        }
    }
}
