package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.DBStatus;

@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index(value = "status"),
                @Index(value = {"remoteId", "accountId"}, unique = true),
        }
)
public abstract class AbstractRemoteEntity extends AbstractAccountRelatedEntity {

    /// Unique per [Account].
    @Nullable
    protected Long remoteId;

    @NonNull
    @Embedded
    protected SynchronizationContext synchronizationContext;

    public AbstractRemoteEntity() {
        // Default constructor
        synchronizationContext = new SynchronizationContext();
    }

    @Ignore
    public AbstractRemoteEntity(@NonNull AbstractRemoteEntity entity) {
        super(entity);
        this.remoteId = entity.getRemoteId();
        this.synchronizationContext = new SynchronizationContext(entity.getSynchronizationContext());
    }

    @Ignore
    @NonNull
    public DBStatus getStatus() {
        return Optional.ofNullable(synchronizationContext.status())
                .orElse(DBStatus.VOID);
    }

    @Ignore
    public void setStatus(@Nullable DBStatus status) {
        synchronizationContext = new SynchronizationContext(status, synchronizationContext.eTag());
    }

    @Ignore
    public void setETag(@Nullable String eTag) {
        synchronizationContext = new SynchronizationContext(synchronizationContext.status(), eTag);
    }

    @Nullable
    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(@Nullable Long remoteId) {
        this.remoteId = remoteId;
    }

    @NonNull
    public SynchronizationContext getSynchronizationContext() {
        return synchronizationContext;
    }

    public void setSynchronizationContext(@NonNull SynchronizationContext synchronizationContext) {
        this.synchronizationContext = synchronizationContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractRemoteEntity that = (AbstractRemoteEntity) o;
        return Objects.equals(remoteId, that.remoteId) && Objects.equals(synchronizationContext, that.synchronizationContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId, synchronizationContext);
    }
}
