package it.niedermann.nextcloud.tables.remote.shared.model;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record DataResponseDto(
        @SerializedName("columnId")
        @Nullable Long remoteColumnId,
        @Nullable JsonElement value
) implements Serializable {
}