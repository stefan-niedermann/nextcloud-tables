package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.DBStatus;

/// [AbstractRemoteEntity] implicitly creates an unique index on [#remoteId] and [#accountId].
/// In case the entity is not unique on an instance, do not inherit super indices.
@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index(value = "status"),
                @Index(value = {"remoteId", "accountId"}, unique = true),
        }
)
public abstract class AbstractRemoteEntity extends AbstractAccountRelatedEntity {

    @Nullable
    protected Long remoteId;

    @Nullable
    protected DBStatus status = DBStatus.VOID;

    public AbstractRemoteEntity() {
        // Default constructor
    }

    @Ignore
    public AbstractRemoteEntity(@NonNull AbstractRemoteEntity entity) {
        super(entity);
        this.remoteId = entity.getRemoteId();
        this.status = entity.status;
    }

    @Nullable
    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(@Nullable Long remoteId) {
        this.remoteId = remoteId;
    }

    @NonNull
    public DBStatus getStatus() {
        assert status != null;
        return status;
    }

    public void setStatus(@NonNull DBStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractRemoteEntity that = (AbstractRemoteEntity) o;
        return Objects.equals(remoteId, that.remoteId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId, status);
    }
}
