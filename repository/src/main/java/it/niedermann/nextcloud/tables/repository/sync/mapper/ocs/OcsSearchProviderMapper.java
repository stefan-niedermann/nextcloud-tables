package it.niedermann.nextcloud.tables.repository.sync.mapper.ocs;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchProvider;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

@Mapper
public interface OcsSearchProviderMapper extends RemoteMapper<OcsSearchProvider, SearchProvider> {

    OcsSearchProviderMapper INSTANCE = Mappers.getMapper(OcsSearchProviderMapper.class);

    @Override
    OcsSearchProvider toDto(SearchProvider entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Override
    SearchProvider toEntity(OcsSearchProvider dto);
}
