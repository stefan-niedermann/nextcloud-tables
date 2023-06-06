package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.Row;

@Dao
public interface RowDao extends GenericDao<Row> {

    @Query("SELECT r.* FROM `Row` r " +
            "INNER JOIN `Table` t " +
            "ON t.id == r.tableId " +
            "WHERE r.accountId = :accountId " +
            "AND (t.isShared == 0 OR t.manage == 1 OR t.`delete` == 1) " +
            "AND r.status = 'LOCAL_DELETED'")
    List<Row> getLocallyDeletedRows(long accountId);

    @Query("SELECT r.* FROM `Row` r " +
            "LEFT JOIN `Table` t " +
            "ON t.id == r.tableId " +
            "WHERE r.accountId = :accountId " +
            "AND (t.isShared == 0 OR t.manage == 1 OR t.`update` == 1) " +
            "AND r.status = 'LOCAL_EDITED'")
    List<Row> getLocallyEditedRows(long accountId);

    // TODO Check for DELETED
    @Query("SELECT * FROM `Row` r " +
            "WHERE r.tableId = :tableId " +
            "AND r.status != 'LOCAL_DELETED' " +
            "ORDER BY r.remoteId IS NULL OR r.remoteId = '', r.remoteId")
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
