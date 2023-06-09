package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Column;

@Dao
public interface ColumnDao extends GenericDao<Column> {

    @Query("SELECT * FROM `Column` " +
            "WHERE id IN (:ids)")
    List<Column> getColumns(Collection<Long> ids);

    @Query("SELECT * FROM `Column` " +
            "WHERE accountId = :accountId " +
            "AND status = :status")
    List<Column> getColumns(long accountId, DBStatus status);

    @Query("SELECT * FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND status != 'LOCAL_DELETED' " +
            "ORDER BY orderWeight DESC")
    LiveData<List<Column>> getNotDeletedColumns$(long tableId);

    @Query("SELECT * FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND status != 'LOCAL_DELETED' " +
            "ORDER BY orderWeight DESC")
    List<Column> getNotDeletedColumns(long tableId);

    @MapInfo(keyColumn = "id", valueColumn = "orderWeight")
    @Query("SELECT id, orderWeight FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND status != 'LOCAL_DELETED' " +
            "ORDER BY orderWeight DESC")
    Map<Long, Integer> getNotDeletedOrderWeights(long tableId);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT remoteId, id FROM `Column` " +
            "WHERE accountId = :accountId " +
            "AND remoteId IN (:remoteIds)")
    Map<Long, Long> getColumnRemoteAndLocalIds(long accountId, Collection<Long> remoteIds);

    @Query("DELETE FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND remoteId NOT IN (:remoteIds)")
    void deleteExcept(long tableId, Collection<Long> remoteIds);

    @Query("UPDATE `Column` " +
            "SET orderWeight = :orderWeight, status = 'LOCAL_EDITED' " +
            "WHERE id = :id " +
            "AND orderWeight != :orderWeight")
    void updateOrderWeight(long id, long orderWeight);
}
