package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.Map;

public record UpdateRowRequestV1Dto(
        @NonNull Map<Long, JsonElement> data
) implements Serializable {
}
