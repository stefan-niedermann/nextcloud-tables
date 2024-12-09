package it.niedermann.nextcloud.tables.repository.sync.mapper.ocs;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

import it.niedermann.nextcloud.tables.database.model.Version;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class OcsVersionMapper implements Mapper<OcsCapabilitiesResponse.OcsVersion, Version> {
    @NonNull
    @Override
    public OcsCapabilitiesResponse.OcsVersion toDto(@NonNull Version entity) {
        final var dto = new OcsCapabilitiesResponse.OcsVersion();
        dto.string = entity.getVersion();
        dto.major = entity.getMajor();
        dto.minor = entity.getMinor();
        dto.macro = entity.getPatch();
        return dto;
    }

    @NonNull
    @Override
    public Version toEntity(@NonNull OcsCapabilitiesResponse.OcsVersion dto) {
        return new Version(dto.string, dto.major, dto.minor, dto.macro);
    }
}
