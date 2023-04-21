package it.niedermann.nextcloud.tables.database.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Column;

@Dao
public interface ColumnDao extends GenericDao<Column> {

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status = :status")
    List<Column> getColumns(long tableId, @NonNull DBStatus status);

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status != 'LOCAL_DELETED'")
    LiveData<List<Column>> getNotDeletedColumns$(long tableId);

//    @Query("SELECT c.remoteId FROM `Column` c JOIN `Row` r ON  WHERE accountId = :accountId ");
//    Long getRemoteId(long accountId, long columnRemoteId);
}
