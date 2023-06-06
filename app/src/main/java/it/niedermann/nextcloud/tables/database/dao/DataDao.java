package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Data;

@Dao
public interface DataDao extends GenericDao<Data> {

    @Query("SELECT d.* FROM Data d " +
            "LEFT JOIN `Row` r ON d.rowId = r.id " +
            "LEFT JOIN `Column` c ON d.columnId = c.id " +
            "WHERE d.rowId = :rowId " +
            "ORDER BY r.remoteId, c.remoteId")
    Data[] getDataForRow(long rowId);

    // TODO Check for DELETED
    @Query("SELECT d.* FROM Data d " +
            "LEFT JOIN `Row` r ON d.rowId = r.id " +
            "LEFT JOIN `Column` c ON d.columnId = c.id " +
            "WHERE r.tableId = :tableId " +
            "AND c.tableId = :tableId " +
            "AND c.status != 'LOCAL_DELETED' " +
            "AND r.status != 'LOCAL_DELETED' " +
            "ORDER BY r.remoteId, c.orderWeight")
    LiveData<List<Data>> getData(long tableId);

    @Query("SELECT * FROM Data d WHERE d.columnId = :columnId AND d.rowId = :rowId")
    Data getDataForCoordinates(long columnId, long rowId);

    @Query("SELECT EXISTS(SELECT id FROM Data WHERE columnId = :columnId AND rowId = :rowId)")
    boolean exists(long columnId, long rowId);
}
