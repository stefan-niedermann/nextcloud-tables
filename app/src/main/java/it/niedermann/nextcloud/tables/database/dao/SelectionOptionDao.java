package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;

@Dao
public interface SelectionOptionDao extends GenericDao<SelectionOption> {

    @Query("SELECT * FROM SelectionOption s WHERE s.columnId = :columnId")
    List<SelectionOption> getSelectionOptions(long columnId);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT s.remoteId, s.id FROM SelectionOption s WHERE s.columnId = :columnId AND s.remoteId IN (:remoteIds)")
    Map<Long, Long> getSelectionOptionRemoteAndLocalIds(long columnId, Collection<Long> remoteIds);

    @Query("DELETE FROM SelectionOption WHERE columnId = :columnId AND remoteId NOT IN (:remoteIds)")
    void deleteExcept(long columnId, Collection<Long> remoteIds);

    // TODO Check for DELETED
    @Query("SELECT DISTINCT s.* FROM SelectionOption s " +
            "INNER JOIN `Column` c ON s.columnId = c.id " +
            "INNER JOIN `Table` t on c.tableId = t.id " +
            "INNER JOIN `Row` r ON r.tableId = t.id " +
            "INNER JOIN Data d ON d.rowId = r.id AND d.columnId = c.id " +
            "WHERE t.id = :tableId " +
            "AND c.status != 'LOCAL_DELETED' " +
            "AND r.status != 'LOCAL_DELETED' "
    )
    LiveData<List<SelectionOption>> getUsedSelectionOptionsById(long tableId);
}
