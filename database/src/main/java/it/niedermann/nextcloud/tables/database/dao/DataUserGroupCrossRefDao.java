package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.DataUserGroupCrossRef;

@Dao
public interface DataUserGroupCrossRefDao {

    @Insert
    long insert(DataUserGroupCrossRef entity);

    @Upsert
    long upsert(DataUserGroupCrossRef entity);

    @Insert
    long[] insert(DataUserGroupCrossRef... entity);

    @Delete
    void delete(DataUserGroupCrossRef... entity);

    @Query("DELETE FROM DataUserGroupCrossRef " +
            "WHERE DataUserGroupCrossRef.dataId = :dataId")
    void delete(long dataId);

    @Query("SELECT crossRef.* " +
            "FROM DataUserGroupCrossRef crossRef " +
            "WHERE crossRef.dataId = :dataId")
    List<DataUserGroupCrossRef> getCrossRefs(long dataId);
}
