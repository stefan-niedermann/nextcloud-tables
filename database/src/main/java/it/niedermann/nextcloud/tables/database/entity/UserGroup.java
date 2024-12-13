package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.model.EUserGroupType;

@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index(value = {"accountId", "remoteId"}, unique = true),
        }
)
public class UserGroup extends AbstractAccountRelatedEntity {

    @Nullable
    protected String remoteId;
    protected String key;
    protected EUserGroupType type;

    public UserGroup() {
        // Default constructor
    }

    @Ignore
    public UserGroup(
            @Nullable String remoteId,
            String key,
            EUserGroupType type
    ) {
        this.remoteId = remoteId;
        this.key = key;
        this.type = type;
    }

    @Ignore
    public UserGroup(@NonNull UserGroup userGroup) {
        super(userGroup);
        this.remoteId = userGroup.getRemoteId();
        this.key = userGroup.getKey();
        this.type = userGroup.getType();
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
