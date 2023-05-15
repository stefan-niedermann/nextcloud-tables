package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Column;

@Dao
public interface ColumnDao extends GenericDao<Column> {

    @Query("SELECT * FROM `Column` c WHERE c.accountId = :accountId AND c.status = :status")
    List<Column> getColumns(long accountId, DBStatus status);

    @Query("SELECT c.* FROM `Column` c WHERE c.id IN (:ids)")
    List<Column> getColumns(long accountId, Collection<Long> ids);

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status != 'LOCAL_DELETED' ORDER BY c.orderWeight")
    LiveData<List<Column>> getNotDeletedColumns$(long tableId);

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status != 'LOCAL_DELETED' ORDER BY c.orderWeight DESC")
    List<Column> getNotDeletedColumns(long tableId);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT c.remoteId, c.id FROM `Column` c WHERE c.accountId = :accountId AND c.remoteId IN (:remoteIds)")
    Map<Long, Long> getColumnRemoteAndLocalIds(long accountId, Collection<Long> remoteIds);

    @Query("DELETE FROM `Column` WHERE tableId = :tableId AND remoteId NOT IN (:remoteIds)")
    void deleteExcept(long tableId, Collection<Long> remoteIds);
}
