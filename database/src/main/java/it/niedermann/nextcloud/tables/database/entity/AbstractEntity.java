package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity()
public abstract class AbstractEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    protected long id;

    @Nullable
    protected String eTag;

    public AbstractEntity() {
        // Default constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getETag() {
        return eTag;
    }

    public void setETag(@Nullable String eTag) {
        this.eTag = eTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return id == that.id && Objects.equals(eTag, that.eTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eTag);
    }
}
