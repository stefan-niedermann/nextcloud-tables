package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.tables.database.entity.OnSharePermission;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.OnSharePermissionV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface OnSharePermissionV2Mapper extends RemoteMapper<OnSharePermissionV2Dto, OnSharePermission> {

    OnSharePermissionV2Mapper INSTANCE = Mappers.getMapper(OnSharePermissionV2Mapper.class);

    @Override
    OnSharePermissionV2Dto toDto(OnSharePermission entity);

    @Override
    OnSharePermission toEntity(OnSharePermissionV2Dto dto);
}
