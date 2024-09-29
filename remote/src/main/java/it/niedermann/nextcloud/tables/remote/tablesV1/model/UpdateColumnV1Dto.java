package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public record UpdateColumnV1Dto(
        @Nullable String title,
        boolean mandatory,
        @Nullable String description,
        @Nullable String numberPrefix,
        @Nullable String numberSuffix,
        @Nullable Double numberDefault,
        @Nullable Double numberMin,
        @Nullable Double numberMax,
        @Nullable Integer numberDecimals,
        @Nullable String textDefault,
        @Nullable String textAllowedPattern,
        @Nullable Integer textMaxLength,
        @Nullable List<SelectionOptionV1Dto> selectionOptions,
        @Nullable String selectionDefault,
        @Nullable String datetimeDefault,
        @Nullable List<UserGroupV1Dto> usergroupDefault,
        @Nullable Boolean usergroupMultipleItems,
        @Nullable Boolean usergroupSelectUsers,
        @Nullable Boolean usergroupSelectGroups,
        @Nullable Boolean showUserStatus
) implements Serializable {
}
