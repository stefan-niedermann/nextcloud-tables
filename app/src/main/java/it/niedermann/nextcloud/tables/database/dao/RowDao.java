package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Row;

@Dao
public interface RowDao extends GenericDao<Row> {

    @Query("SELECT * FROM `Row` r WHERE r.accountId = :accountId AND r.status = :status")
    List<Row> getRows(long accountId, DBStatus status);

    @Query("SELECT * FROM `Row` r WHERE r.tableId = :tableId AND r.status != 'LOCAL_DELETED' ORDER BY r.remoteId IS NULL OR r.remoteId = '', r.remoteId")
    LiveData<List<Row>> getNotDeletedRows$(long tableId);

    @Query("SELECT * FROM `Row` WHERE id = :id")
    Row get(long id);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT r.remoteId, r.id FROM `Row` r WHERE r.accountId = :accountId AND r.remoteId IN (:remoteIds)")
    Map<Long, Long> getRowRemoteAndLocalIds(long accountId, Collection<Long> remoteIds);

    @Query("DELETE FROM `Row` WHERE tableId = :tableId")
    void deleteAllFromTable(long tableId);

    @Query("DELETE FROM `Row` WHERE tableId = :tableId AND remoteId NOT IN (:remoteIds)")
    void deleteExcept(long tableId, Collection<Long> remoteIds);
}
