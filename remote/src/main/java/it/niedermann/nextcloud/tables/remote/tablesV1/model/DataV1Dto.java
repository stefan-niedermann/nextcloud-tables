package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record DataV1Dto(
        @SerializedName("columnId")
        @Nullable Long remoteColumnId,
        @Nullable JsonElement value
) implements Serializable {
}
