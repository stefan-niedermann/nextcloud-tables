package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Table;

@Dao
public interface TableDao extends GenericDao<Table> {

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId AND t.status = :status")
    List<Table> getTables(long accountId, DBStatus status);

    @Query("SELECT * FROM `Table` t WHERE t.id = :id")
    Table getTable(long id);

    @Query("SELECT * FROM `Table` t WHERE t.id = :id AND t.status != 'LOCAL_DELETED' ORDER by t.title")
    LiveData<Table> getNotDeletedTable$(long id);

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId AND t.status != 'LOCAL_DELETED' LIMIT 1")
    Table getAnyNotDeletedTables(long accountId);

    @Query("SELECT * FROM `Table` t WHERE t.accountId = :accountId AND t.isShared = :isShared AND t.status != 'LOCAL_DELETED' ORDER by t.title")
    LiveData<List<Table>> getNotDeletedTables$(long accountId, boolean isShared);

    @Query("UPDATE `Table` SET status = :status WHERE id = :id")
    void updateStatus(long id, DBStatus status);
}
