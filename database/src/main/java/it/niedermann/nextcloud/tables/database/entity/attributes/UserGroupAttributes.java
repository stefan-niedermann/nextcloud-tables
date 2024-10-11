package it.niedermann.nextcloud.tables.database.entity.attributes;

public record UserGroupAttributes(
        boolean usergroupMultipleItems,
        boolean usergroupSelectUsers,
        boolean usergroupSelectGroups,
        boolean showUserStatus
) {
}
