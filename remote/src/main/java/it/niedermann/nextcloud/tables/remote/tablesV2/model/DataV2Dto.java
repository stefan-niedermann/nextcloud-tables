package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record DataV2Dto(
        @SerializedName("columnId")
        @Nullable Long remoteColumnId,
        @Nullable JsonElement value
) implements Serializable {
}