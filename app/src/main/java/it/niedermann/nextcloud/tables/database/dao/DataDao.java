package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Data;

@Dao
public interface DataDao extends GenericDao<Data> {

    @Query("SELECT * FROM Data d WHERE d.accountId = :accountId AND d.remoteColumnId = :remoteColumnIds")
    LiveData<List<Data>> getData(long accountId, List<Long> remoteColumnIds);
}
