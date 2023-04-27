package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Data;

@Dao
public interface DataDao extends GenericDao<Data> {

    @Query("SELECT * FROM Data d " +
            "INNER JOIN `Row` r ON d.rowId = r.id " +
            "INNER JOIN `Column` c ON d.columnId = c.id " +
            "WHERE d.rowId = :rowId " +
            "ORDER BY r.remoteId, c.remoteId")
    Data[] getDataForRow(long rowId);

    @Query("SELECT * FROM Data d " +
            "INNER JOIN `Row` r ON d.rowId = r.id " +
            "INNER JOIN `Column` c ON d.columnId = c.id " +
            "WHERE r.tableId = :tableId " +
            "AND c.tableId = :tableId " +
            "ORDER BY r.remoteId, c.remoteId")
    LiveData<List<Data>> getData(long tableId);

    @Query("SELECT * FROM Data d WHERE d.columnId = :columnId AND d.rowId = :rowId")
    Data getDataForCoordinates(long columnId, long rowId);

}
