package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.TableV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

@Mapper(uses = OnSharePermissionV2Mapper.class)
public interface TableV2Mapper extends RemoteMapper<TableV2Dto, Table> {

    TableV2Mapper INSTANCE = Mappers.getMapper(TableV2Mapper.class);

    @Mapping(source = "shared", target = "isShared")
    @Mapping(source = "onSharePermission", target = "onSharePermissions")
    @Override
    TableV2Dto toDto(Table entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "ETag", ignore = true)
    @Mapping(target = "synchronizationContext", ignore = true)
    @Mapping(target = "currentRow", ignore = true)
    @InheritInverseConfiguration
    @Override
    Table toEntity(TableV2Dto dto);
}
