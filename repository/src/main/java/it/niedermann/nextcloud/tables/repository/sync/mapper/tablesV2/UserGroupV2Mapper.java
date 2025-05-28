package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.UserGroupV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

@Mapper(uses = EUserGroupTypeV2Mapper.class)
public interface UserGroupV2Mapper extends RemoteMapper<UserGroupV2Dto, UserGroup> {

    UserGroupV2Mapper INSTANCE = Mappers.getMapper(UserGroupV2Mapper.class);

    @Mapping(source = "displayName", target = "key")
    @Override
    UserGroupV2Dto toDto(UserGroup entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @InheritInverseConfiguration
    @Override
    UserGroup toEntity(UserGroupV2Dto dto);
}
