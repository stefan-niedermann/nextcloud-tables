package it.niedermann.nextcloud.tables.database.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import it.niedermann.nextcloud.tables.database.entity.AbstractEntity;

public interface GenericDao<T extends AbstractEntity> {

    @Insert
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
