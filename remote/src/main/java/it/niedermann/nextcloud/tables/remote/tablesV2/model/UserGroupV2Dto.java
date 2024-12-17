package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public record UserGroupV2Dto(
        @SerializedName("id")
        String remoteId,
        @NonNull
        String key,
        EUserGroupTypeV2Dto type
) implements Serializable {
}
