package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Table extends AbstractRemoteEntity {
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String title = "";
    @ColumnInfo(defaultValue = "")
    private String emoji;
    @ColumnInfo(defaultValue = "")
    private String ownership;
    @ColumnInfo(defaultValue = "")
    private String ownerDisplayName;
    @ColumnInfo(defaultValue = "")
    private String createdBy;
    private Instant createdAt;
    @ColumnInfo(defaultValue = "")
    private String lastEditBy;
    @ColumnInfo(defaultValue = "")
    private String lastEditAt;
    private boolean isShared;
    @Embedded
    @SerializedName("onSharePermissions")
    private OnSharePermission onSharePermission;

    public Table() {
        // Default constructor
    }

    @NonNull
    public String getTitleWithEmoji() {
        return String.format(Locale.getDefault(), "%s %s", getEmoji(), getTitle()).trim();
    }

    public boolean hasReadPermission() {
        return !isShared || getOnSharePermission().isManage() || getOnSharePermission().isRead();
    }

    public boolean hasCreatePermission() {
        return !isShared || getOnSharePermission().isManage() || getOnSharePermission().isCreate();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasUpdatePermission() {
        return !isShared || getOnSharePermission().isManage() || getOnSharePermission().isUpdate();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasDeletePermission() {
        return !isShared || getOnSharePermission().isManage() || getOnSharePermission().isDelete();
    }

    public boolean hasManagePermission() {
        return !isShared || getOnSharePermission().isManage();
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastEditBy() {
        return lastEditBy;
    }

    public void setLastEditBy(String lastEditBy) {
        this.lastEditBy = lastEditBy;
    }

    public String getLastEditAt() {
        return lastEditAt;
    }

    public void setLastEditAt(String lastEditAt) {
        this.lastEditAt = lastEditAt;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public OnSharePermission getOnSharePermission() {
        return onSharePermission;
    }

    public void setOnSharePermission(OnSharePermission onSharePermission) {
        this.onSharePermission = onSharePermission;
    }

    @Override
    @NonNull
    public String toString() {
        return getTitleWithEmoji();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Table table = (Table) o;
        return isShared == table.isShared && title.equals(table.title) && Objects.equals(emoji, table.emoji) && Objects.equals(ownership, table.ownership) && Objects.equals(ownerDisplayName, table.ownerDisplayName) && Objects.equals(createdBy, table.createdBy) && Objects.equals(createdAt, table.createdAt) && Objects.equals(lastEditBy, table.lastEditBy) && Objects.equals(lastEditAt, table.lastEditAt) && Objects.equals(onSharePermission, table.onSharePermission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, emoji, ownership, ownerDisplayName, createdBy, createdAt, lastEditBy, lastEditAt, isShared, onSharePermission);
    }
}
