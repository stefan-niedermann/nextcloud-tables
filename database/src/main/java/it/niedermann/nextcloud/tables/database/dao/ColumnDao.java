package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullColumn;

@Dao
public interface ColumnDao extends GenericDao<Column> {

    @Transaction
    @Query("SELECT * FROM `Column` " +
            "WHERE accountId = :accountId " +
            "AND status = :status")
    List<FullColumn> getFullColumns(long accountId, DBStatus status);

    @Transaction
    @Query("SELECT * FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND status != 'LOCAL_DELETED' " +
            "ORDER BY orderWeight DESC")
    LiveData<List<FullColumn>> getNotDeletedFullColumns$(long tableId);

    @Transaction
    @Query("SELECT * FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND status != 'LOCAL_DELETED' " +
            "ORDER BY orderWeight DESC")
    List<FullColumn> getNotDeletedColumns(long tableId);

    @MapInfo(keyColumn = "remoteId")
    @Query("SELECT * FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND status != 'LOCAL_DELETED' " +
            "ORDER BY orderWeight DESC")
    Map<Long, Column> getNotDeletedRemoteIdsAndColumns(long tableId);


    @MapInfo(keyColumn = "id", valueColumn = "orderWeight")
    @Query("SELECT id, orderWeight FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND status != 'LOCAL_DELETED' " +
            "ORDER BY orderWeight DESC")
    Map<Long, Integer> getNotDeletedOrderWeights(long tableId);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT remoteId, id FROM `Column` " +
            "WHERE tableId = :tableId " +
            "AND remoteId IN (:remoteIds)")
    Map<Long, Long> getColumnRemoteAndLocalIds(long tableId, Collection<Long> remoteIds);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT remoteId, id FROM `Column` " +
            "WHERE tableId = :tableId")
    Map<Long, Long> getColumnRemoteAndLocalIds(long tableId);

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
