package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;

@Dao
public interface DataDao extends GenericDao<Data> {

    @Transaction
    @MapInfo(keyColumn = "columnId")
    @Query("SELECT d.* FROM Data d " +
            "LEFT JOIN `Row` r ON d.rowId = r.id " +
            "LEFT JOIN `Column` c ON d.columnId = c.id " +
            "WHERE d.rowId = :rowId " +
            "ORDER BY r.remoteId, c.remoteId")
    Map<Long, FullData> getColumnIdAndFullData(long rowId);

    @Query("SELECT d.* FROM Data d " +
            "LEFT JOIN `Row` r ON d.rowId = r.id " +
            "LEFT JOIN `Column` c ON d.columnId = c.id " +
            "WHERE r.tableId = :tableId " +
            "AND c.tableId = :tableId " +
            "AND c.status IS NOT 'LOCAL_DELETED' " +
            "AND r.status IS NOT 'LOCAL_DELETED' " +
            "ORDER BY r.remoteId, c.orderWeight")
    LiveData<List<Data>> getData(long tableId);

    @Query("SELECT d.id FROM Data d " +
            "WHERE d.rowId = :rowId " +
            "AND d.remoteColumnId = :remoteColumnId ")
    Long getDataIdForCoordinates(long remoteColumnId, long rowId);

    @Query("DELETE FROM Data " +
            "WHERE rowId = :rowId " +
            "AND data_booleanValue IS NULL " +
            "AND data_dateValue IS NULL " +
            "AND data_doubleValue IS NULL " +
            "AND data_instantValue IS NULL " +
            "AND data_stringValue IS NULL " +
            "AND data_timeValue IS NULL " +
            "AND id NOT IN (" +
            "SELECT xRef.dataId FROM DataSelectionOptionCrossRef xRef " +
            "WHERE xRef.dataId = id " +
            ")")
    void deleteRowIfEmpty(long rowId);

    @Query("SELECT EXISTS(SELECT d.id FROM Data d WHERE d.columnId = :columnId AND d.rowId = :rowId)")
    boolean exists(long columnId, long rowId);
}
