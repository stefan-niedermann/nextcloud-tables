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
            "WHERE d.accountId = :accountId " +
            "AND d.rowId = :rowId " +
            "AND r.tableId = :tableId " +
            "AND c.tableId = :tableId " +
            "ORDER BY r.remoteId, c.remoteId")
    Data[] getData(long accountId, long tableId, long rowId);

    @Query("SELECT * FROM Data d " +
            "INNER JOIN `Row` r ON d.rowId = r.id " +
            "INNER JOIN `Column` c ON d.columnId = c.id " +
            "WHERE d.accountId = :accountId " +
            "AND r.tableId = :tableId " +
            "AND c.tableId = :tableId " +
            "ORDER BY r.remoteId, c.remoteId")
    LiveData<List<Data>> getData(long accountId, long tableId);
}
