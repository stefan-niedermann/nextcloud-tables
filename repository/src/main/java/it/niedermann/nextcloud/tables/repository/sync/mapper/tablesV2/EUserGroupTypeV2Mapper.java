package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EUserGroupTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class EUserGroupTypeV2Mapper implements Mapper<EUserGroupTypeV2Dto, EUserGroupType> {

    @NonNull
    @Override
    public EUserGroupTypeV2Dto toDto(@NonNull EUserGroupType entity) {
        return switch (entity) {
            case USER -> EUserGroupTypeV2Dto.USER;
            case GROUP -> EUserGroupTypeV2Dto.GROUP;
            case TEAM -> EUserGroupTypeV2Dto.TEAMS;
            case UNKNOWN ->
                    throw new IllegalArgumentException(EUserGroupType.UNKNOWN.name() + " " + EUserGroupType.class.getSimpleName() + " can not be mapped to API");
        };
    }

    @NonNull
    @Override
    public EUserGroupType toEntity(@NonNull EUserGroupTypeV2Dto dto) {
        return switch (dto) {
            case USER -> EUserGroupType.USER;
            case GROUP -> EUserGroupType.GROUP;
            case TEAMS -> EUserGroupType.TEAM;
        };
    }
}
