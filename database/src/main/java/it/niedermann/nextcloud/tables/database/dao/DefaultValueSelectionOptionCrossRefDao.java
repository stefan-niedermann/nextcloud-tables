package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.DefaultValueSelectionOptionCrossRef;

@Dao
public interface DefaultValueSelectionOptionCrossRefDao {

    @Query("""
            SELECT x.* FROM DefaultValueSelectionOptionCrossRef x
            INNER JOIN `Column` c
            ON x.columnId = c.id
            INNER JOIN SelectionOption s
            ON x.selectionOptionId = s.id
            WHERE c.id = :columnId
            """)
    List<DefaultValueSelectionOptionCrossRef> loadByColumn(long columnId);

    @Query("""
            SELECT x.* FROM DefaultValueSelectionOptionCrossRef x
            INNER JOIN `Column` c
            ON x.columnId = c.id
            INNER JOIN SelectionOption s
            ON x.selectionOptionId = s.id
            WHERE s.id = :selectionOptionId
            """)
    List<DefaultValueSelectionOptionCrossRef> loadBySelectionOption(long selectionOptionId);

    @Query("""
            SELECT * FROM DefaultValueSelectionOptionCrossRef x
            """)
    List<DefaultValueSelectionOptionCrossRef> load();

    @Insert
    long insert(DefaultValueSelectionOptionCrossRef entity);

    @Insert
    long[] insert(DefaultValueSelectionOptionCrossRef... entity);

    @Delete
    void delete(DefaultValueSelectionOptionCrossRef... entity);
}
