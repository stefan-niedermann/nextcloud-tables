package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.Objects;

/// [AbstractRemoteEntity] implicitly creates an unique index on [#remoteId] and [#accountId].
/// In case the entity is not unique on an instance, do not inherit super indices.
@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index(value = {"remoteId", "accountId"}, unique = true),
        }
)
public abstract class AbstractRemoteEntity extends AbstractAccountRelatedEntity {

    @Nullable
    protected Long remoteId;

    public AbstractRemoteEntity() {
        // Default constructor
    }

    @Nullable
    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(@Nullable Long remoteId) {
        this.remoteId = remoteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractRemoteEntity that = (AbstractRemoteEntity) o;
        return Objects.equals(remoteId, that.remoteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId);
    }
}
