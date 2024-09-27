package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record SelectionOptionV1Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @Nullable String label
) implements Serializable {
}
