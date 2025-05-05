package it.niedermann.nextcloud.tables.database.entity.attributes;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.io.Serializable;

public record UserGroupAttributes(
        boolean usergroupMultipleItems,
        boolean usergroupSelectUsers,
        boolean usergroupSelectGroups,
        @ColumnInfo(defaultValue = "0")
        boolean usergroupSelectTeams,
        boolean showUserStatus
) implements Serializable {

    @Ignore
    public UserGroupAttributes() {
        this(false, false, false, false, false);
    }
}
