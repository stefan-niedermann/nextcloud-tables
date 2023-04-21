package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

public interface GenericDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T entity);

    @SuppressWarnings("unchecked")
    @Insert
    long[] insert(T... entity);

    @SuppressWarnings("unchecked")
    @Update
    void update(T... entity);

    @SuppressWarnings("unchecked")
    @Delete
    void delete(T... entity);
}
