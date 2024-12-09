package it.niedermann.nextcloud.tables.database.entity.attributes;

import java.io.Serializable;

public record UserGroupAttributes(
        boolean usergroupMultipleItems,
        boolean usergroupSelectUsers,
        boolean usergroupSelectGroups,
        boolean showUserStatus
) implements Serializable {
}
