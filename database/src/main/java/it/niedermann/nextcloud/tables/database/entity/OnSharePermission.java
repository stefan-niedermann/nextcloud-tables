package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;
import java.util.Objects;

@Entity(inheritSuperIndices = true)
public class OnSharePermission implements Serializable {
    private boolean read;
    private boolean create;
    private boolean update;
    private boolean delete;
    private boolean manage;

    public OnSharePermission() {
        // Default constructor
    }

    @Ignore
    public OnSharePermission(@NonNull OnSharePermission onSharePermission) {
        this.read = onSharePermission.isRead();
        this.create = onSharePermission.isCreate();
        this.update = onSharePermission.isUpdate();
        this.delete = onSharePermission.isDelete();
        this.manage = onSharePermission.isManage();
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isManage() {
        return manage;
    }

    public void setManage(boolean manage) {
        this.manage = manage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnSharePermission that = (OnSharePermission) o;
        return read == that.read && create == that.create && update == that.update && delete == that.delete && manage == that.manage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(read, create, update, delete, manage);
    }
}
