package it.niedermann.nextcloud.tables.remote.model.columns;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;

public class UserGroupColumn extends AbstractColumn {

    private final List<String> usergroupDefault;
    private final boolean usergroupMultipleItems;
    private final boolean usergroupSelectUsers;
    private final boolean usergroupSelectGroups;

    public UserGroupColumn(long tableRemoteId, @NonNull Column column) {
        super(tableRemoteId, column);
        this.usergroupDefault = column.getUsergroupDefault();
        this.usergroupMultipleItems = column.isUsergroupMultipleItems();
        this.usergroupSelectUsers = column.isUsergroupSelectUsers();
        this.usergroupSelectGroups = column.isUsergroupSelectGroups();
    }

    public List<String> getUsergroupDefault() {
        return usergroupDefault;
    }

    public boolean isUsergroupMultipleItems() {
        return usergroupMultipleItems;
    }

    public boolean isUsergroupSelectUsers() {
        return usergroupSelectUsers;
    }

    public boolean isUsergroupSelectGroups() {
        return usergroupSelectGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserGroupColumn that = (UserGroupColumn) o;
        return usergroupMultipleItems == that.usergroupMultipleItems && usergroupSelectUsers == that.usergroupSelectUsers && usergroupSelectGroups == that.usergroupSelectGroups && Objects.equals(usergroupDefault, that.usergroupDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), usergroupDefault, usergroupMultipleItems, usergroupSelectUsers, usergroupSelectGroups);
    }
}
