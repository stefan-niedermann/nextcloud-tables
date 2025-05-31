package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EUserGroupTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

@Mapper
public interface EUserGroupTypeV2Mapper extends RemoteMapper<EUserGroupTypeV2Dto, EUserGroupType> {

    EUserGroupTypeV2Mapper INSTANCE = Mappers.getMapper(EUserGroupTypeV2Mapper.class);

    @ValueMapping(source = "TEAM", target = "TEAMS")
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    @ValueMapping(source = MappingConstants.NULL, target = MappingConstants.THROW_EXCEPTION)
    @Override
    EUserGroupTypeV2Dto toDto(EUserGroupType entity);

    @InheritInverseConfiguration
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    @ValueMapping(source = MappingConstants.NULL, target = "UNKNOWN")
    @Override
    EUserGroupType toEntity(EUserGroupTypeV2Dto dto);
}
