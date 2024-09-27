package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;

public record RowV1Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @Nullable String createdBy,
        @Nullable Instant createdAt,
        @Nullable String lastEditBy,
        @Nullable Instant lastEditAt,
        @Nullable DataV1Dto[] data
) implements Serializable {
}
