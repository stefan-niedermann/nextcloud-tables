package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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
                @Index(value = {"accountId", "remoteId"}, unique = true),
        }
)
public class UserGroup extends AbstractAccountRelatedEntity {

    @Nullable
    protected String remoteId;
    protected String displayName;
    protected EUserGroupType type;

    public UserGroup() {
        // Default constructor
    }

    @Ignore
    public UserGroup(
            @Nullable String remoteId,
            String displayName,
            EUserGroupType type
    ) {
        this.remoteId = remoteId;
        this.displayName = displayName;
        this.type = type;
    }

    @Ignore
    public UserGroup(@NonNull UserGroup userGroup) {
        super(userGroup);
        this.remoteId = userGroup.getRemoteId();
        this.displayName = userGroup.getDisplayName();
        this.type = userGroup.getType();
    }

    @Nullable
    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(@Nullable String remoteId) {
        this.remoteId = remoteId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
        return Objects.equals(remoteId, userGroup.remoteId) && Objects.equals(displayName, userGroup.displayName) && type == userGroup.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId, displayName, type);
    }
}
