package it.niedermann.nextcloud.tables.database.dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import it.niedermann.nextcloud.tables.database.entity.LinkValue;

/// @noinspection NullableProblems
@Dao
public interface LinkValueDao extends GenericDao<LinkValue> {

    @Query("""
            DELETE FROM LinkValue
            WHERE LinkValue.dataId = :dataId
            """)
    void delete(long dataId);

    @Transaction
    default void insertLinkValueAndUpdateData(@NonNull LinkValue linkValue) {
        insert(linkValue);
        updateLinkValueRef(linkValue.getDataId());
    }

    @Transaction
    default void upsertLinkValueAndUpdateData(@NonNull LinkValue linkValue) {
        upsert(linkValue);
        updateLinkValueRef(linkValue.getDataId());
    }

    @Query("""
            UPDATE Data
            SET linkValueRef = :dataId
            WHERE id = :dataId
            """)
    void updateLinkValueRef(@Nullable Long dataId);

    @Query("""
            SELECT lv.*
            FROM LinkValue lv
            WHERE lv.dataId = :dataId
            """)
    LinkValue findById(long dataId);

    @Transaction
    @Query("""
            SELECT EXISTS(
                SELECT lv.dataId
                FROM LinkValue lv
                WHERE lv.dataId = :dataId
                LIMIT 1
            )
            """)
    boolean exists(Long dataId);
}
