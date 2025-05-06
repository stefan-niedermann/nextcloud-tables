package it.niedermann.nextcloud.tables.database.dao;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapColumn;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullTable;

@Dao
public interface TableDao extends GenericDao<Table> {

    @Query("SELECT t.* FROM `Table` t WHERE t.accountId = :accountId")
    List<Table> getTables(long accountId);

    @Query("SELECT t.* FROM `Table` t WHERE t.accountId = :accountId AND (t.isShared == 0 OR t.manage == 1 OR t.read == 1)")
    List<Table> getTablesWithReadPermission(long accountId);

    @Query("SELECT t.* FROM `Table` t WHERE t.accountId = :accountId AND t.status IS :status")
    List<Table> getTables(long accountId, DBStatus status);

    @Query("SELECT t.* FROM `Table` t WHERE t.accountId = :accountId AND t.status IS 'LOCAL_EDITED' AND t.remoteId IS NULL")
    List<Table> getLocallyCreatedTables(long accountId);

    @Query("SELECT t.* FROM `Table` t WHERE t.accountId = :accountId AND t.status IS 'LOCAL_EDITED' AND t.remoteId IS NOT NULL")
    List<Table> getLocallyEditedTables(long accountId);

    @Query("SELECT t.* FROM `Table` t WHERE t.accountId = :accountId AND t.status IS 'LOCAL_DELETED'")
    List<Table> getLocallyDeletedTables(long accountId);

    @Query("SELECT t.* FROM `Table` t WHERE t.id = :id")
    Table getTable(long id);

    @Query("SELECT t.id FROM `Table` t WHERE t.accountId = :accountId")
    List<Long> getTableIdsPerAccounts(long accountId);

    @Query("SELECT remoteId FROM `Table` WHERE id = :id")
    Long getRemoteId(long id);

    @Query("SELECT t.* FROM `Table` t " +
            "WHERE t.id = :id " +
            "AND t.status IS NOT 'LOCAL_DELETED' " +
            "ORDER by t.title")
    LiveData<Table> getNotDeletedTable$(long id);

    @Query("SELECT t.id FROM `Table` t WHERE t.accountId = :accountId AND t.status IS NOT 'LOCAL_DELETED' LIMIT 1")
    Long getAnyNotDeletedTableId(long accountId);

    @Query("""
            SELECT t.* FROM `Table` t
            WHERE t.accountId = :accountId
            AND t.favorite = :favorite
            AND t.archived = :archived
            AND t.status IS NOT 'LOCAL_DELETED'
            ORDER by t.createdAt
            """)
    LiveData<List<Table>> getNotDeletedTables$(long accountId, boolean favorite, boolean archived);

    @Query("SELECT t.remoteId, t.id FROM `Table` t WHERE t.accountId = :accountId AND t.remoteId IN (:remoteIds)")
    Map<@MapColumn(columnName = "remoteId") Long, @MapColumn(columnName = "id") Long> getTableRemoteAndLocalIds(long accountId, Collection<Long> remoteIds);

    @Query("DELETE FROM `Table` WHERE accountId = :accountId AND remoteId NOT IN (:remoteIds)")
    void deleteExcept(long accountId, Collection<Long> remoteIds);

    @Transaction
    @Query("SELECT t.*, COUNT(allRows.id) as rowCount FROM `Table` t " +
            "LEFT JOIN `Row` allRows " +
            "ON allRows.id = t.id " +
            "LEFT JOIN (" +
            "   SELECT r.*" +
            "   FROM `Row` r " +
            "   LIMIT :limit " +
            "   OFFSET :offset" +
            ") queriedRows " +
            "ON t.id = queriedRows.tableId " +
            "WHERE t.id = :tableId " +
            "AND t.status IS NOT 'LOCAL_DELETED'" +
            "LIMIT 1")
    LiveData<FullTable> getFullTable$(long tableId, long offset, long limit);

    @Query("UPDATE `Table` " +
            "SET currentRow = :currentRow " +
            "WHERE id = :tableId")
    void updateCurrentRow(long tableId, @Nullable Long currentRow);
}
