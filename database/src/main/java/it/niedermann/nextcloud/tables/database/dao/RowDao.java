package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapColumn;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullRow;

@Dao
public interface RowDao extends GenericDao<Row> {

    @Query("DELETE FROM `Row` WHERE id = :id")
    void delete(long id);

    @Query("SELECT t.* FROM `Table` t " +
            "INNER JOIN `Row` r " +
            "ON t.id = r.tableId " +
            "WHERE t.accountId = :accountId " +
            "AND t.status IS NULL " +
            "AND r.accountId = :accountId " +
            "AND r.remoteId IS NULL " +
            "AND r.status IS 'LOCAL_EDITED'")
    List<Table> getUnchangedTablesHavingLocallyCreatedRows(long accountId);

    @Query("SELECT t.* FROM `Table` t " +
            "INNER JOIN `Row` r " +
            "ON t.id = r.tableId " +
            "WHERE t.accountId = :accountId " +
            "AND t.status IS NULL " +
            "AND r.accountId = :accountId " +
            "AND r.remoteId IS NOT NULL " +
            "AND r.status IS 'LOCAL_EDITED' ")
    List<Table> getUnchangedTablesHavingLocallyEditedRowsOrChangedOrDeletedData(long accountId);

    @Query("SELECT t.* FROM `Table` t " +
            "INNER JOIN `Row` r " +
            "ON t.id = r.tableId " +
            "WHERE t.accountId = :accountId " +
            "AND t.status IS NULL " +
            "AND r.accountId = :accountId " +
            "AND r.status IS 'LOCAL_DELETED'")
    List<Table> getUnchangedTablesHavingLocallyDeletedRows(long accountId);

    @Query("SELECT r.* FROM `Row` r " +
            "INNER JOIN `Table` t " +
            "ON t.id == r.tableId " +
            "WHERE r.accountId = :accountId " +
            "AND (t.isShared == 0 OR t.manage == 1 OR t.`delete` == 1) " +
            "AND t.id = :tableId " +
            "AND r.status IS 'LOCAL_DELETED'")
    List<Row> getLocallyDeletedRows(long accountId, long tableId);

    @Transaction
    @Query("SELECT r.* FROM `Row` r " +
            "INNER JOIN `Table` t " +
            "ON t.id == r.tableId " +
            "WHERE t.accountId = :accountId " +
            "AND t.id = :tableId " +
            "AND (t.isShared == 0 OR t.manage == 1 OR t.`update` == 1) " +
            "AND r.accountId = :accountId " +
            "AND r.remoteId IS NOT NULL " +
            "AND r.status IS 'LOCAL_EDITED'")
    List<FullRow> getLocallyEditedRows(long accountId, long tableId);

    @Transaction
    @Query("SELECT r.* FROM `Row` r " +
            "INNER JOIN `Table` t " +
            "ON t.id == r.tableId " +
            "WHERE r.accountId = :accountId " +
            "AND (t.isShared == 0 OR t.manage == 1 OR t.`update` == 1) " +
            "AND t.id = :tableId " +
            "AND r.remoteId IS NULL " +
            "AND r.status IS 'LOCAL_EDITED'")
    List<FullRow> getLocallyCreatedRows(long accountId, long tableId);

    @Transaction
    @Query("SELECT r.* FROM `Row` r " +
            "WHERE r.tableId = :tableId " +
            "AND r.status IS NOT 'LOCAL_DELETED' " +
            "ORDER BY r.createdAt DESC")
    LiveData<List<FullRow>> getNotDeletedRows$(long tableId);

    @Query("SELECT r.* FROM `Row` r WHERE r.id = :id")
    Row get(long id);

    @Query("SELECT r.remoteId, r.id FROM `Row` r WHERE r.tableId = :tableId")
    Map<@MapColumn(columnName = "remoteId") Long, @MapColumn(columnName = "id") Long> getRowRemoteAndLocalIds(long tableId);

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
