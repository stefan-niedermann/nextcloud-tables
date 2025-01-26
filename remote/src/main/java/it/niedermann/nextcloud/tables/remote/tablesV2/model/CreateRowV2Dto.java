package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.Map;

public record CreateRowV2Dto(
        @NonNull Map<Long, JsonElement> data
) implements Serializable {
}
