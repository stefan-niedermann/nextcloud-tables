package it.niedermann.nextcloud.tables.features.column.edit;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.SearchProvider;

@FunctionalInterface
public interface SearchProviderSupplier {

    @NonNull
    LiveData<List<SearchProvider>> getSearchProvider(long accountId);
}