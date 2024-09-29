package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.EUserGroupTypeV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UserGroupV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class UserGroupV1Mapper implements Mapper<UserGroupV1Dto, UserGroup> {

    private final Mapper<EUserGroupTypeV1Dto, EUserGroupType> mapper;

    public UserGroupV1Mapper() {
        this(new EUserGroupTypeV1Mapper());
    }

    private UserGroupV1Mapper(@NonNull Mapper<EUserGroupTypeV1Dto, EUserGroupType> mapper) {
        this.mapper = mapper;
    }

    @NonNull
    @Override
    public UserGroupV1Dto toDto(@NonNull UserGroup entity) {
        return new UserGroupV1Dto(
                entity.getRemoteId(),
                entity.getKey(),
                mapper.toDto(entity.getType())
        );
    }

    @NonNull
    @Override
    public UserGroup toEntity(@NonNull UserGroupV1Dto dto) {
        final var entity = new UserGroup();
        entity.setRemoteId(dto.remoteId());
        entity.setKey(dto.key());
        entity.setType(mapper.toEntity(dto.type()));
        return entity;
    }
}
