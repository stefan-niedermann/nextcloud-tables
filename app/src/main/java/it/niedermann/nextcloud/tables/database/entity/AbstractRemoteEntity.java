package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"accountId", "id", "remoteId"}, unique = true),
                @Index(value = {"accountId", "remoteId"}, unique = true),
                @Index(value = {"accountId", "id"}, unique = true)
        }
)
public abstract class AbstractRemoteEntity extends AbstractEntity {

    @SerializedName("id")
    protected long remoteId;

    @Expose(deserialize = false, serialize = false)
    protected long accountId;

    public AbstractRemoteEntity() {
        // Default constructor
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
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
        AbstractRemoteEntity that = (AbstractRemoteEntity) o;
        return remoteId == that.remoteId && accountId == that.accountId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId, accountId);
    }
}
