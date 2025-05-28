package it.niedermann.nextcloud.tables.repository.sync.mapper.ocs;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse.OcsVersion;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.tables.database.model.Version;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

@Mapper
public interface OcsVersionMapper extends RemoteMapper<OcsVersion, Version> {

    OcsVersionMapper INSTANCE = Mappers.getMapper(OcsVersionMapper.class);

    @Mapping(source = "version", target = "string")
    @Mapping(source = "patch", target = "macro")
    @Mapping(target = "edition", ignore = true)
    @Mapping(target = "extendedSupport", ignore = true)
    OcsVersion toDto(Version entity);

    @InheritInverseConfiguration
    Version toEntity(OcsVersion dto);
}
