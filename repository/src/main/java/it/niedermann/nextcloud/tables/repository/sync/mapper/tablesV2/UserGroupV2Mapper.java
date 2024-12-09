package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EUserGroupTypeV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.UserGroupV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class UserGroupV2Mapper implements Mapper<UserGroupV2Dto, UserGroup> {

    private final Mapper<EUserGroupTypeV2Dto, EUserGroupType> mapper;

    public UserGroupV2Mapper() {
        this(new EUserGroupTypeV2Mapper());
    }

    private UserGroupV2Mapper(@NonNull Mapper<EUserGroupTypeV2Dto, EUserGroupType> mapper) {
        this.mapper = mapper;
    }

    @NonNull
    @Override
    public UserGroupV2Dto toDto(@NonNull UserGroup entity) {
        return new UserGroupV2Dto(
                entity.getRemoteId(),
                entity.getKey(),
                mapper.toDto(entity.getType()));
    }

    @NonNull
    @Override
    public UserGroup toEntity(@NonNull UserGroupV2Dto dto) {
        return new UserGroup(
                dto.remoteId(),
                dto.key(),
                mapper.toEntity(dto.type()));
    }
}
