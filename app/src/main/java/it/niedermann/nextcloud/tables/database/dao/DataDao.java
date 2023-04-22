package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Data;

@Dao
public interface DataDao extends GenericDao<Data> {

    @MapInfo(keyColumn = "id")
    @Query("SELECT * FROM Data d WHERE d.accountId = :accountId AND d.tableId = :tableId ORDER BY d.remoteRowId, d.remoteColumnId")
    LiveData<List<Data>> getData(long accountId, long tableId);
}
