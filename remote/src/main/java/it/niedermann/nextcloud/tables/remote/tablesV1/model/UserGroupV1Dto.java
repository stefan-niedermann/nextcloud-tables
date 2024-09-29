package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record UserGroupV1Dto(
        @SerializedName("id")
        String remoteId,
        @NonNull
        String key,
        EUserGroupTypeV1Dto type
) implements Serializable {
}
