package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapColumn;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.SearchProvider;

@Dao
public interface SearchProviderDao extends GenericDao<SearchProvider> {

    @Query("""
            SELECT sp.*
            FROM SearchProvider sp
            WHERE sp.accountId = :accountId
            """)
    Map<@MapColumn(tableName = "sp", columnName = "remoteId") String,
            @MapColumn(tableName = "sp", columnName = "id") Long>
    getRemoteIdToLocalId(long accountId);

    @Query("""
            SELECT sp.*
            FROM SearchProvider sp
            WHERE sp.accountId = :accountId
            ORDER BY sp.`order`
            """)
    LiveData<List<SearchProvider>> getSearchProvider(long accountId);

    @Query("""
            SELECT sp.*
            FROM SearchProvider sp
            WHERE sp.accountId = :accountId
            AND sp.remoteId in (:allowedSearchProviders)
            ORDER BY sp.`order`
            """)
    List<SearchProvider> getSearchProvider(long accountId, Collection<String> allowedSearchProviders);

    @Query("""
            SELECT sp.id
            FROM SearchProvider sp
            WHERE sp.accountId = :accountId
            AND sp.remoteId = :remoteId
            LIMIT 1
            """)
    Long getSearchProviderId(long accountId, String remoteId);
}
