package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import com.google.gson.annotations.SerializedName;

public record UpdateRowResponseV1Dto(
        @SerializedName("id")
        long remoteId
) {
}
