package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.UserGroupV2Dto;

public class CreateUserGroupColumnV2Dto extends CreateColumnV2Dto {

    private final List<UserGroupV2Dto> usergroupDefault;
    private final boolean usergroupMultipleItems;
    private final boolean usergroupSelectUsers;
    private final boolean usergroupSelectGroups;
    private final boolean usergroupSelectTeams;

    public CreateUserGroupColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto dto) {
        super(tableRemoteId, dto);
        this.usergroupDefault = Optional.ofNullable(dto.usergroupDefault()).orElse(Collections.emptyList());
        this.usergroupMultipleItems = Boolean.TRUE.equals(dto.usergroupMultipleItems());
        this.usergroupSelectUsers = Boolean.TRUE.equals(dto.usergroupSelectUsers());
        this.usergroupSelectGroups = Boolean.TRUE.equals(dto.usergroupSelectGroups());
        this.usergroupSelectTeams = Boolean.TRUE.equals(dto.usergroupSelectTeams());
    }

    public List<UserGroupV2Dto> getUsergroupDefault() {
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

    public boolean isUsergroupSelectTeams() {
        return usergroupSelectTeams;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreateUserGroupColumnV2Dto that = (CreateUserGroupColumnV2Dto) o;
        return usergroupMultipleItems == that.usergroupMultipleItems && usergroupSelectUsers == that.usergroupSelectUsers && usergroupSelectGroups == that.usergroupSelectGroups && usergroupSelectTeams == that.usergroupSelectTeams && Objects.equals(usergroupDefault, that.usergroupDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), usergroupDefault, usergroupMultipleItems, usergroupSelectUsers, usergroupSelectGroups, usergroupSelectTeams);
    }
}
