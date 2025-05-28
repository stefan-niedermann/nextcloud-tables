package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.SelectionOptionV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

@Mapper
public interface SelectionOptionV2Mapper extends RemoteMapper<SelectionOptionV2Dto, SelectionOption> {

    SelectionOptionV2Mapper INSTANCE = Mappers.getMapper(SelectionOptionV2Mapper.class);

    @Override
    SelectionOptionV2Dto toDto(SelectionOption entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "columnId", ignore = true)
    @InheritInverseConfiguration
    SelectionOption toEntity(SelectionOptionV2Dto dto);
}
