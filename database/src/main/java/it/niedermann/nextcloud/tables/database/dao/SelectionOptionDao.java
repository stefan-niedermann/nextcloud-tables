package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Dao;
import androidx.room.MapColumn;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;

@Dao
public interface SelectionOptionDao extends GenericDao<SelectionOption> {

    @Query("SELECT s.* " +
            "FROM SelectionOption s " +
            "WHERE s.columnId = :columnId")
    List<SelectionOption> getSelectionOptions(long columnId);

    @Query("SELECT s.remoteId, s.id " +
            "FROM SelectionOption s " +
            "WHERE s.columnId = :columnId " +
            "AND s.remoteId IN (:remoteColumnIds)")
    Map<@MapColumn(columnName = "remoteId") Long, @MapColumn(columnName = "id") Long> getSelectionOptionRemoteColumnAndLocalIds(long columnId, Collection<Long> remoteColumnIds);

    @Query("DELETE FROM SelectionOption " +
            "WHERE columnId = :columnId " +
            "AND remoteId NOT IN (:remoteColumnIds)")
    void deleteExcept(long columnId, Collection<Long> remoteColumnIds);
}
