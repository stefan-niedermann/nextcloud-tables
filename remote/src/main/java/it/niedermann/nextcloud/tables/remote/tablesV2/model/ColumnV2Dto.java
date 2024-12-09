package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import it.niedermann.nextcloud.tables.remote.shared.model.RemoteDto;

public record ColumnV2Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @NonNull String title,
        @Nullable Instant createdAt,
        @Nullable String createdBy,
        @Nullable String lastEditBy,
        @Nullable Instant lastEditAt,
        @Nullable String type,
        @Nullable String subtype,
        @Nullable Boolean mandatory,
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
        @Nullable List<SelectionOptionV2Dto> selectionOptions,
        @Nullable JsonElement selectionDefault,
        @Nullable String datetimeDefault,
        @Nullable List<UserGroupV2Dto> usergroupDefault,
        @Nullable Boolean usergroupMultipleItems,
        @Nullable Boolean usergroupSelectUsers,
        @Nullable Boolean usergroupSelectGroups,
        @Nullable Boolean showUserStatus
) implements Serializable, RemoteDto {
}
