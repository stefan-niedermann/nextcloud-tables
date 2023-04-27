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

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status = :status")
    List<Column> getColumns(long tableId, DBStatus status);

    @Query("SELECT * FROM `Column` c WHERE c.tableId = :tableId AND c.status != 'LOCAL_DELETED' ORDER BY c.remoteId")
    LiveData<List<Column>> getNotDeletedColumns$(long tableId);

    @MapInfo(keyColumn = "remoteId", valueColumn = "id")
    @Query("SELECT c.remoteId, c.id FROM `Column` c WHERE c.accountId = :accountId AND c.remoteId IN (:remoteColumnId)")
    Map<Long, Long> getColumnIds(long accountId, Collection<Long> remoteColumnId);
}
