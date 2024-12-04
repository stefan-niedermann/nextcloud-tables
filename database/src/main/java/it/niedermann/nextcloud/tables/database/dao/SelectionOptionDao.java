package it.niedermann.nextcloud.tables.database.dao;

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

    @Query("SELECT * FROM SelectionOption s " +
            "WHERE s.columnId = :columnId " +
            "AND status != 'LOCAL_DELETED'")
    List<SelectionOption> getNotDeletedSelectionOptions(long columnId);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT s.remoteId, s.id " +
            "FROM SelectionOption s " +
            "WHERE s.columnId = :columnId " +
            "AND s.remoteId IN (:remoteIds)")
    Map<Long, Long> getSelectionOptionRemoteAndLocalIds(long columnId, Collection<Long> remoteIds);

    @Query("DELETE FROM SelectionOption " +
            "WHERE columnId = :columnId " +
            "AND remoteId NOT IN (:remoteIds)")
    void deleteExcept(long columnId, Collection<Long> remoteIds);
}
