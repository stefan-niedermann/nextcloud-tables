package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Table;

@Dao
public interface TableDao extends GenericDao<Table> {

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId")
    List<Table> getTables(long accountId);

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId AND (t.isShared == 0 OR t.manage == 1 OR t.read == 1)")
    List<Table> getTablesWithReadPermission(long accountId);

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId AND t.status = :status")
    List<Table> getTables(long accountId, DBStatus status);

    @Query("SELECT * FROM `Table` t WHERE t.id = :id")
    Table getTable(long id);

    @Query("SELECT remoteId FROM `Table` WHERE id = :id")
    Long getRemoteId(long id);

    @Query("SELECT * FROM `Table` t WHERE t.id = :id AND t.status != 'LOCAL_DELETED' ORDER by t.title")
    LiveData<Table> getNotDeletedTable$(long id);

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId AND t.status != 'LOCAL_DELETED' LIMIT 1")
    Table getAnyNotDeletedTables(long accountId);

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId AND t.isShared = :isShared AND t.status != 'LOCAL_DELETED' ORDER by t.title")
    LiveData<List<Table>> getNotDeletedTables$(long accountId, boolean isShared);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT t.remoteId, t.id FROM `Table` t WHERE t.accountId = :accountId AND t.remoteId IN (:remoteIds)")
    Map<Long, Long> getTableRemoteAndLocalIds(long accountId, Collection<Long> remoteIds);

    @Query("DELETE FROM `Table` WHERE accountId = :accountId AND remoteId NOT IN (:remoteIds)")
    void deleteExcept(long accountId, Collection<Long> remoteIds);
}
