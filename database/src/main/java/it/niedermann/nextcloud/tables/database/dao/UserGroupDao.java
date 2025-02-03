package it.niedermann.nextcloud.tables.database.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import it.niedermann.nextcloud.tables.database.entity.UserGroup;

@Dao
public interface UserGroupDao extends GenericDao<UserGroup> {

    @Query("SELECT EXISTS(" +
            "   SELECT u.id " +
            "   FROM UserGroup u " +
            "   WHERE u.accountId = :accountId " +
            "   AND u.remoteId = :remoteId " +
            "   LIMIT 1" +
            ")")
    boolean exists(long accountId, String remoteId);

    @Query("SELECT u.id " +
            "FROM UserGroup u " +
            "WHERE u.accountId = :accountId " +
            "AND u.remoteId = :remoteId"
            )
    Long getIdByRemoteId(long accountId, String remoteId);

    @Transaction
    default Long upsertAndGetId(@NonNull UserGroup userGroup) {
        upsert(userGroup);
        return getIdByRemoteId(userGroup.getAccountId(), userGroup.getRemoteId());
    }
}
