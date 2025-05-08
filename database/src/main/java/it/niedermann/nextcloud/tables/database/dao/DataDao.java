package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapColumn;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;

@Dao
public interface DataDao extends GenericDao<Data> {

    @Transaction
    @Query("""
            SELECT d.*
            FROM Data d
            LEFT JOIN `Row` r ON d.rowId = r.id
            LEFT JOIN `Column` c ON d.columnId = c.id
            WHERE d.rowId = :rowId
            ORDER BY r.remoteId, c.remoteId
            """)
    Map<@MapColumn(columnName = "columnId") Long, FullData> getColumnIdAndFullData(long rowId);

    @Query("""
            SELECT d.* FROM Data d
            LEFT JOIN `Row` r ON d.rowId = r.id
            LEFT JOIN `Column` c ON d.columnId = c.id
            WHERE r.tableId = :tableId
            AND c.tableId = :tableId
            AND c.status IS NOT 'LOCAL_DELETED'
            AND r.status IS NOT 'LOCAL_DELETED'
            ORDER BY r.createdAt DESC, d.stringValue, d.instantValue, d.doubleValue, d.dateValue, d.booleanValue, d.timeValue
            """)
    LiveData<List<Data>> getData(long tableId);

    @Query("""
            SELECT d.id
            FROM Data d
            WHERE d.rowId = :rowId
            AND d.remoteColumnId = :remoteColumnId
            """)
    Long getDataIdForCoordinates(long remoteColumnId, long rowId);

    @Transaction
    @Query("""
            DELETE FROM Data
            WHERE rowId = :rowId
            AND booleanValue IS NULL
            AND dateValue IS NULL
            AND doubleValue IS NULL
            AND instantValue IS NULL
            AND stringValue IS NULL
            AND timeValue IS NULL
            AND linkValueRef IS NULL
            AND id NOT IN (
                SELECT xRef.dataId
                FROM DataSelectionOptionCrossRef xRef
                WHERE xRef.dataId = id
            )
            """)
    void deleteRowIfEmpty(long rowId);

    @Transaction
    @Query("""
            SELECT EXISTS(
                SELECT d.id
                FROM Data d
                WHERE d.columnId = :columnId
                AND d.rowId = :rowId
                LIMIT 1
            )
            """)
    boolean exists(long columnId, long rowId);

    // TODO maybe this should be a database trigger?
    @Query("""
            UPDATE DATA
            SET remoteColumnId = :remoteColumnId
            WHERE columnId = :columnId
            """)
    void updateColumnRemoteIds(long columnId, long remoteColumnId);
}
