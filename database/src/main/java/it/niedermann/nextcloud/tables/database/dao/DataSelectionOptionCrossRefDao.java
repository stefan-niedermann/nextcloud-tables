package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.DataSelectionOptionCrossRef;

@Dao
public interface DataSelectionOptionCrossRefDao {

    @Insert
    long insert(DataSelectionOptionCrossRef entity);

    @Insert
    long[] insert(DataSelectionOptionCrossRef... entity);

    @Upsert
    long upsert(DataSelectionOptionCrossRef entity);

    @Delete
    void delete(DataSelectionOptionCrossRef... entity);

    @Query("""
            SELECT crossRef.*
            FROM DataSelectionOptionCrossRef crossRef
            WHERE crossRef.dataId = :dataId
            """)
    List<DataSelectionOptionCrossRef> getCrossRefs(long dataId);
}
