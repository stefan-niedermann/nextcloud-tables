package it.niedermann.nextcloud.tables.remote.tablesV1.adapter;

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

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;

/**
 * Used for parsing and serializing non-user content like {@link Row#getCreatedAt()}, {@link Row#getLastEditAt()}, {@link Column#getLastEditAt()} or {@link Column#getLastEditAt()}
 */
public class InstantV1Adapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    private final DateTimeFormatter dateTimeFormatter;

    public InstantV1Adapter(@NonNull DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public JsonElement serialize(Instant date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateTimeFormatter.format(date));
    }

    @Override
    public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return LocalDateTime
                .parse(jsonElement.getAsString(), dateTimeFormatter)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }
}