package it.niedermann.nextcloud.tables.remote.adapter;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    private final DateTimeFormatter dateTimeFormatter;

    public InstantAdapter(@NonNull String patternDateTime) {
        dateTimeFormatter = DateTimeFormatter.ofPattern(patternDateTime);
    }

    @Override
    public synchronized JsonElement serialize(Instant date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateTimeFormatter.format(date));
    }

    @Override
    public synchronized Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return LocalDateTime
                    .parse(jsonElement.getAsString(), dateTimeFormatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}