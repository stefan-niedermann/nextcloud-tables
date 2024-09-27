package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;

public class CreateUserGroupColumnV2Dto extends CreateColumnV2Dto {

    private final List<String> usergroupDefault;
    private final boolean usergroupMultipleItems;
    private final boolean usergroupSelectUsers;
    private final boolean usergroupSelectGroups;

    public CreateUserGroupColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        super(tableRemoteId, column);
        this.usergroupDefault = column.usergroupDefault();
        this.usergroupMultipleItems = column.usergroupMultipleItems();
        this.usergroupSelectUsers = column.usergroupSelectUsers();
        this.usergroupSelectGroups = column.usergroupSelectGroups();
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
        CreateUserGroupColumnV2Dto that = (CreateUserGroupColumnV2Dto) o;
        return usergroupMultipleItems == that.usergroupMultipleItems && usergroupSelectUsers == that.usergroupSelectUsers && usergroupSelectGroups == that.usergroupSelectGroups && Objects.equals(usergroupDefault, that.usergroupDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), usergroupDefault, usergroupMultipleItems, usergroupSelectUsers, usergroupSelectGroups);
    }
}
