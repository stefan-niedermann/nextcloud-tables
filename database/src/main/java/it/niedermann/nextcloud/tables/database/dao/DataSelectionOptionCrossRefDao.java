package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.DataSelectionOptionCrossRef;

@Dao
public interface DataSelectionOptionCrossRefDao {

    @Insert
    long insert(DataSelectionOptionCrossRef entity);

    @Insert
    long[] insert(DataSelectionOptionCrossRef... entity);

    @Delete
    void delete(DataSelectionOptionCrossRef... entity);

    @Query("SELECT cf.* " +
            "FROM DataSelectionOptionCrossRef cf " +
            "WHERE cf.dataId = :dataId")
    List<DataSelectionOptionCrossRef> getCrossRefs(long dataId);
}
