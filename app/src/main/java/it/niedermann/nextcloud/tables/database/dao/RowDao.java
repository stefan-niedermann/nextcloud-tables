package it.niedermann.nextcloud.tables.database.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Row;

@Dao
public interface RowDao extends GenericDao<Row> {

    @Query("SELECT * FROM `Row` r WHERE r.tableId = :tableId AND r.status = :status")
    List<Row> getRows(long tableId, @NonNull DBStatus status);

    @Query("SELECT * FROM `Row` r WHERE r.tableId = :tableId AND r.status != 'LOCAL_DELETED'")
    LiveData<List<Row>> getNotDeletedRows$(long tableId);
}
