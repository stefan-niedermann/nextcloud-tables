package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.Index;

import com.google.gson.annotations.Expose;

import java.util.Objects;

@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index(value = {"accountId", "id"}, unique = true)
        }
)
public abstract class AbstractAccountRelatedEntity extends AbstractEntity {


    @Expose(deserialize = false, serialize = false)
    protected long accountId;

    public AbstractAccountRelatedEntity() {
        // Default constructor
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
