package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapColumn;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;

@Dao
public interface ColumnDao extends GenericDao<Column> {

    @Transaction
    @Query("""
            SELECT c.*
            FROM `Column` c
            """)
    List<Column> getColumns();

    @Query("""
            SELECT t.*
            FROM `Table` t
            INNER JOIN `Column` c
            ON t.id = c.tableId
            WHERE t.accountId = :accountId
            AND t.status IS NULL
            AND c.accountId = :accountId
            AND c.remoteId IS NULL
            AND c.status IS 'LOCAL_EDITED'
            """)
    List<Table> getUnchangedTablesHavingLocallyCreatedColumns(long accountId);

    @Query("""
            SELECT t.*
            FROM `Table` t
            INNER JOIN `Column` c ON t.id = c.tableId
            WHERE t.accountId = :accountId
            AND t.status IS NULL
            AND c.accountId = :accountId
            AND c.remoteId IS NOT NULL
            AND c.status IS 'LOCAL_EDITED'
            """)
    List<Table> getUnchangedTablesHavingLocallyEditedColumnsOrChangedOrDeletedSelectionOptions(long accountId);

    @Query("""
            SELECT t.*
            FROM `Table` t
            INNER JOIN `Column` c
            ON t.id = c.tableId
            WHERE t.accountId = :accountId
            AND t.status IS NULL
            AND c.accountId = :accountId
            AND c.status IS 'LOCAL_DELETED'
            """)
    List<Table> getUnchangedTablesHavingLocallyDeletedColumns(long accountId);

    @Transaction
    @Query("""
            SELECT c.*
            FROM `Column` c
            WHERE accountId = :accountId
            AND tableId = :tableId
            AND remoteId IS NULL
            AND status IS 'LOCAL_EDITED'
            """)
    List<FullColumn> getLocallyCreatedColumns(long accountId, long tableId);

    @Transaction
    @Query("""
            SELECT c.*
            FROM `Column` c
            WHERE accountId = :accountId
            AND tableId = :tableId
            AND remoteId IS NOT NULL
            AND status IS 'LOCAL_EDITED'
            """)
    List<FullColumn> getLocallyEditedColumns(long accountId, long tableId);

    @Transaction
    @Query("""
            SELECT c.*
            FROM `Column` c
            WHERE accountId = :accountId
            AND tableId = :tableId
            AND status IS 'LOCAL_DELETED'
            """)
    List<FullColumn> getLocallyDeletedColumns(long accountId, long tableId);

    @Transaction
    @Query("""
            SELECT c.*
            FROM `Column` c
            WHERE tableId = :tableId
            AND status IS NOT 'LOCAL_DELETED'
            ORDER BY orderWeight DESC
            """)
    LiveData<List<FullColumn>> getNotDeletedFullColumns$(long tableId);

    @Transaction
    @Query("""
            SELECT c.*
            FROM `Column` c
            WHERE tableId = :tableId
            AND status IS NOT 'LOCAL_DELETED'
            ORDER BY orderWeight DESC
            """)
    List<FullColumn> getNotDeletedColumns(long tableId);

    @Transaction
    @Query("""
            SELECT c.*
            FROM `Column` c
            WHERE c.tableId = :tableId
            AND c.status IS NOT 'LOCAL_DELETED'
            ORDER BY c.orderWeight DESC
            """)
    Map<@MapColumn(columnName = "remoteId") Long, FullColumn> getNotDeletedColumnRemoteIdsAndFullColumns(long tableId);

    @Query("""
            SELECT c.remoteId AS columnRemoteId, s.*
            FROM `SelectionOption` s
            INNER JOIN `Column` c
            ON s.columnId = c.id
            WHERE c.tableId = :tableId
            AND c.status IS NOT 'LOCAL_DELETED'
            """)
    Map<@MapColumn(columnName = "columnRemoteId") Long, List<SelectionOption>> getNotDeletedSelectionOptions(long tableId);

    @Query("""
            SELECT id, orderWeight
            FROM `Column`
            WHERE tableId = :tableId
            AND status IS NOT 'LOCAL_DELETED'
            ORDER BY orderWeight DESC
            """)
    Map<
            @MapColumn(columnName = "id") Long,
            @MapColumn(columnName = "orderWeight") Integer> getNotDeletedOrderWeights(long tableId);

    @Query("""
            SELECT remoteId, id
            FROM `Column`
            WHERE tableId = :tableId
            AND remoteId IN (:remoteIds)
            """)
    Map<
            @MapColumn(columnName = "remoteId") Long,
            @MapColumn(columnName = "id") Long> getColumnRemoteAndLocalIds(long tableId, Collection<Long> remoteIds);

    @Query("""
            DELETE FROM `Column`
            WHERE tableId = :tableId
            AND remoteId NOT IN (:remoteIds)
            """)
    void deleteExcept(long tableId, Collection<Long> remoteIds);

    @Query("""
            UPDATE `Column`
            SET orderWeight = :orderWeight, status = 'LOCAL_EDITED'
            WHERE id = :id
            AND orderWeight != :orderWeight
            """)
    void updateOrderWeight(long id, long orderWeight);
}
