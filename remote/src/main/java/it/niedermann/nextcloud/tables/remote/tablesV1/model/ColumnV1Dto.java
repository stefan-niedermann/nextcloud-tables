package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record ColumnV1Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @NonNull String title,
        @Nullable Instant createdAt,
        @Nullable String createdBy,
        @Nullable String lastEditBy,
        @Nullable Instant lastEditAt,
        @Nullable String type,
        @Nullable String subtype,
        boolean mandatory,
        @Nullable String description,
        @Nullable Double numberDefault,
        @Nullable Double numberMin,
        @Nullable Double numberMax,
        @Nullable Integer numberDecimals,
        @Nullable String numberPrefix,
        @Nullable String numberSuffix,
        @Nullable String textDefault,
        @Nullable String textAllowedPattern,
        @Nullable Integer textMaxLength,
        @Nullable List<SelectionOptionV1Dto> selectionOptions,
        @Nullable JsonElement selectionDefault,
        @Nullable String datetimeDefault,
        @Nullable List<UserGroupV1Dto> usergroupDefault,
        @Nullable Boolean usergroupMultipleItems,
        @Nullable Boolean usergroupSelectUsers,
        @Nullable Boolean usergroupSelectGroups,
        @Nullable Boolean showUserStatus
) implements Serializable {
}
