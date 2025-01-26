package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Upsert;

public interface GenericDao<T> {

    @Insert
    long insert(T entity);

    @SuppressWarnings("unchecked")
    @Insert
    long[] insert(T... entity);

    @SuppressWarnings("unchecked")
    @Update
    void update(T... entity);

    /**
     * @noinspection unchecked
     */
    @Upsert
    void upsert(T... entity);

    @SuppressWarnings("unchecked")
    @Delete
    void delete(T... entity);
}
