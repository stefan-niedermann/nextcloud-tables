package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record SelectionOptionV2Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @Nullable String label
) implements Serializable {
}
