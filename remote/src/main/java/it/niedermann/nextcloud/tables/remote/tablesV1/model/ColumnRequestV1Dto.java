package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record ColumnRequestV1Dto(
        @Nullable String title,
        boolean mandatory,
        @Nullable String description,

        @Nullable String textDefault,
        @Nullable String textAllowedPattern,
        @Nullable Integer textMaxLength,

        @Nullable Double numberDefault,
        @Nullable String numberPrefix,
        @Nullable String numberSuffix,
        @Nullable Double numberMin,
        @Nullable Double numberMax,
        @Nullable Integer numberDecimals,

        @Nullable String datetimeDefault,

        // Yes, selection default and options are a double-encoded JSON string. No, I don't know why.
        @Nullable String selectionDefault,
        @Nullable String selectionOptions,

        @Nullable List<UserGroupV1Dto> usergroupDefault,
        @Nullable Boolean usergroupMultipleItems,
        @Nullable Boolean usergroupSelectUsers,
        @Nullable Boolean usergroupSelectGroups,
        @Nullable Boolean showUserStatus

) {

    public record UserGroupV1Dto(
            @SerializedName("id")
            String remoteId,
            @NonNull
            String key,
            EUserGroupTypeV1Dto type

    ) {
    }
}
