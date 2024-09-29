package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.EUserGroupTypeV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class EUserGroupTypeV1Mapper implements Mapper<EUserGroupTypeV1Dto, EUserGroupType> {

    @NonNull
    @Override
    public EUserGroupTypeV1Dto toDto(@NonNull EUserGroupType entity) {
        return switch (entity) {
            case USER -> EUserGroupTypeV1Dto.USER;
            case GROUP -> EUserGroupTypeV1Dto.GROUP;
            case CIRCLE -> EUserGroupTypeV1Dto.CIRCLE;
            case UNKNOWN ->
                    throw new IllegalArgumentException(EUserGroupType.UNKNOWN.name() + " " + EUserGroupType.class.getSimpleName() + " can not be mapped to API");
        };
    }

    @NonNull
    @Override
    public EUserGroupType toEntity(@NonNull EUserGroupTypeV1Dto dto) {
        return switch (dto) {
            case USER -> EUserGroupType.USER;
            case GROUP -> EUserGroupType.GROUP;
            case CIRCLE -> EUserGroupType.CIRCLE;
        };
    }
}
