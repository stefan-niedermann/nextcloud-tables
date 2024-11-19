package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.model.FullRow;

@Dao
public interface RowDao extends GenericDao<Row> {

    @Query("DELETE FROM `Row` WHERE id = :id")
    void delete(long id);

    @Query("SELECT r.* FROM `Row` r " +
            "INNER JOIN `Table` t " +
            "ON t.id == r.tableId " +
            "WHERE r.accountId = :accountId " +
            "AND (t.isShared == 0 OR t.manage == 1 OR t.`delete` == 1) " +
            "AND r.status = 'LOCAL_DELETED'")
    List<Row> getLocallyDeletedRows(long accountId);

    @Transaction
    @Query("SELECT r.* FROM `Row` r " +
            "LEFT JOIN `Table` t " +
            "ON t.id == r.tableId " +
            "WHERE r.accountId = :accountId " +
            "AND (t.isShared == 0 OR t.manage == 1 OR t.`update` == 1) " +
            "AND r.status = 'LOCAL_EDITED'")
    List<FullRow> getLocallyEditedRows(long accountId);

    @Transaction
    @Query("SELECT * FROM `Row` r " +
            "WHERE r.tableId = :tableId " +
            "AND r.status != 'LOCAL_DELETED' " +
            "ORDER BY r.remoteId IS NULL OR r.remoteId = '', r.remoteId")
    LiveData<List<FullRow>> getNotDeletedRows$(long tableId);

    @Query("SELECT * FROM `Row` WHERE id = :id")
    Row get(long id);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT r.remoteId, r.id FROM `Row` r WHERE r.tableId = :tableId")
    Map<Long, Long> getRowRemoteAndLocalIds(long tableId);

    @Query("SELECT r.id FROM `Row` r WHERE r.tableId = :tableId")
    List<Long> getIds(long tableId);

    @Query("SELECT r.id FROM `Row` r " +
            "WHERE r.tableId = :tableId " +
            "AND r.remoteId = :remoteId " +
            "LIMIT 1")
    Long getRowId(long tableId, long remoteId);

    @Query("DELETE FROM `Row` WHERE tableId = :tableId")
    void deleteAllFromTable(long tableId);
}
