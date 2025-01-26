package it.niedermann.nextcloud.tables.repository.sync.mapper.ocs;

import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchProvider;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class OcsSearchProviderMapper implements Mapper<OcsSearchProvider, SearchProvider> {

    @NonNull
    @Override
    public OcsSearchProvider toDto(@NonNull SearchProvider entity) {
        return new OcsSearchProvider(
                entity.getRemoteId(),
                entity.getAppId(),
                entity.getName(),
                entity.getIcon(),
                entity.getOrder(),
                entity.isInAppSearch());
    }

    @NonNull
    @Override
    public SearchProvider toEntity(@NonNull OcsSearchProvider dto) {
        final var searchProvider = new SearchProvider();
        searchProvider.setRemoteId(requireNonNull(dto.remoteId()));
        searchProvider.setAppId(dto.appId());
        searchProvider.setName(dto.name());
        searchProvider.setIcon(dto.icon());
        searchProvider.setOrder(dto.order());
        searchProvider.setInAppSearch(dto.inAppSearch());
        return searchProvider;
    }
}
