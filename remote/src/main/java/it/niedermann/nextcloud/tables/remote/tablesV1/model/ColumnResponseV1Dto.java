package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import com.google.gson.annotations.SerializedName;

public record ColumnResponseV1Dto(
        @SerializedName("id")
        long remoteId
) {
}
