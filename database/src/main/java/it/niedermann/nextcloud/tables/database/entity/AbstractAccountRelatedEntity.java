package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index(value = "accountId"),
        }
)
public abstract class AbstractAccountRelatedEntity extends AbstractEntity {

    protected long accountId;

    public AbstractAccountRelatedEntity() {
        // Default constructor
    }

    @Ignore
    public AbstractAccountRelatedEntity(@NonNull AbstractAccountRelatedEntity entity) {
        super(entity);
        this.accountId = entity.getAccountId();
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractAccountRelatedEntity that = (AbstractAccountRelatedEntity) o;
        return accountId == that.accountId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accountId);
    }
}
