package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.Collection;

import it.niedermann.nextcloud.tables.database.entity.DefaultValueSelectionOptionCrossRef;

@Dao
public interface DefaultValueSelectionOptionCrossRefDao {

    @Insert
    long insert(DefaultValueSelectionOptionCrossRef entity);

    @Insert
    long[] insert(DefaultValueSelectionOptionCrossRef... entity);

    @Upsert
    long upsert(DefaultValueSelectionOptionCrossRef entity);

    @Delete
    void deleteExcept(DefaultValueSelectionOptionCrossRef... entity);

    @Query("""
            DELETE FROM DefaultValueSelectionOptionCrossRef
            WHERE columnId = :columnId
            AND selectionOptionId IN (:selectionOptionIds)
            """)
    void delete(long columnId, Collection<Long> selectionOptionIds);

    @Query("""
            DELETE FROM DefaultValueSelectionOptionCrossRef
            WHERE columnId = :columnId
            AND selectionOptionId NOT IN (:selectionOptionIds)
            """)
    void deleteExcept(long columnId, Collection<Long> selectionOptionIds);
}
