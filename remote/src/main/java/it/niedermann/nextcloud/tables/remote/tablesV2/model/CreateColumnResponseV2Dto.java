package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record CreateColumnResponseV2Dto(
        @SerializedName("id")
        @Nullable Long remoteId
) implements Serializable {
}
