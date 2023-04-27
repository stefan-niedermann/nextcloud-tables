package it.niedermann.nextcloud.tables.database.dao;

import androidx.annotation.NonNull;
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

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status = :status")
    List<Column> getColumns(long tableId, @NonNull DBStatus status);

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status != 'LOCAL_DELETED' ORDER BY c.remoteId")
    LiveData<List<Column>> getNotDeletedColumns$(long tableId);

    @Query("SELECT c.id FROM `Column` c WHERE c.accountId = :accountId AND c.remoteId = :remoteColumnId")
    long getColumnId(long accountId, long remoteColumnId);

    @MapInfo(keyColumn = "c.remoteId", valueColumn = "c.id")
    @Query("SELECT c.id, c.remoteId FROM `Column` c WHERE c.accountId = :accountId AND c.remoteId = :remoteColumnId")
    Map<Long, Long> getColumnIds(long accountId, Collection<Long> remoteColumnId);
}
