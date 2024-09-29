package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.model.EUserGroupType;

@Entity(
        inheritSuperIndices = true,
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
                @Index(name = "IDX_COLUMN_ACCOUNT_ID_REMOTE_D", value = {"accountId", "remoteId"}, unique = true)
        }
)
public class UserGroup extends AbstractAccountRelatedEntity {
    @Nullable
    protected String remoteId;
    protected String key;
    protected EUserGroupType type;

    public UserGroup() {

    }

    @Nullable
    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(@Nullable String remoteId) {
        this.remoteId = remoteId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EUserGroupType getType() {
        return type;
    }

    public void setType(EUserGroupType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserGroup userGroup = (UserGroup) o;
        return Objects.equals(remoteId, userGroup.remoteId) && Objects.equals(key, userGroup.key) && type == userGroup.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId, key, type);
    }
}
