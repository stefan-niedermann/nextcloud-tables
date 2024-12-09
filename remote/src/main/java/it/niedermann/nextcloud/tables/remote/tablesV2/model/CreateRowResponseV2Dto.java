package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record CreateRowResponseV2Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @Nullable String createdBy,
        @Nullable Instant createdAt,
        @Nullable String lastEditBy,
        @Nullable Instant lastEditAt,
        @Nullable List<DataV2Dto> data
) implements Serializable {
}
