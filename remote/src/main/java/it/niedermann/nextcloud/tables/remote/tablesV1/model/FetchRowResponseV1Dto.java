package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import it.niedermann.nextcloud.tables.remote.shared.model.DataResponseDto;

public record FetchRowResponseV1Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @Nullable String createdBy,
        @Nullable Instant createdAt,
        @Nullable String lastEditBy,
        @Nullable Instant lastEditAt,
        @Nullable List<DataResponseDto> data
) implements Serializable {
}
