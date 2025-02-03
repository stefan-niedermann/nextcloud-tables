package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Row.class,
                        parentColumns = "id",
                        childColumns = "currentRow",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index(value = "title"),
                @Index(value = "isShared"),
                @Index(value = "manage"),
                @Index(value = "read"),
                @Index(value = "currentRow")
        }
)
public class Table extends AbstractRemoteEntity {

    @NonNull
    @ColumnInfo(defaultValue = "")
    private String title = "";

    @ColumnInfo(defaultValue = "")
    @Nullable
    private String description;

    @ColumnInfo(defaultValue = "")
    private String emoji = "";

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
    private Instant lastEditAt;

    private boolean isShared;

    @NonNull
    @Embedded
    private OnSharePermission onSharePermission;

    @Nullable
    private Long currentRow;

    public Table() {
        this.onSharePermission = new OnSharePermission();
    }

    @Ignore
    public Table(@NonNull Table table) {
        super(table);
        this.title = table.getTitle();
        this.description = table.getDescription();
        this.emoji = table.getEmoji();
        this.ownership = table.getOwnership();
        this.ownerDisplayName = table.getOwnerDisplayName();
        this.createdBy = table.getCreatedBy();
        this.createdAt = table.getCreatedAt();
        this.lastEditBy = table.lastEditBy;
        this.lastEditAt = table.lastEditAt;
        this.isShared = table.isShared();
        this.onSharePermission = new OnSharePermission(table.getOnSharePermission());
        this.currentRow = table.getCurrentRow();
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

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @NonNull
    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(@Nullable String emoji) {
        this.emoji = Optional.ofNullable(emoji).orElse("");
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

    public Instant getLastEditAt() {
        return lastEditAt;
    }

    public void setLastEditAt(Instant lastEditAt) {
        this.lastEditAt = lastEditAt;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    @NonNull
    public OnSharePermission getOnSharePermission() {
        return onSharePermission;
    }

    public void setOnSharePermission(@NonNull OnSharePermission onSharePermission) {
        this.onSharePermission = onSharePermission;
    }

    @Nullable
    public Long getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(@Nullable Long currentRow) {
        this.currentRow = currentRow;
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
        return isShared == table.isShared && Objects.equals(title, table.title) && Objects.equals(description, table.description) && Objects.equals(emoji, table.emoji) && Objects.equals(ownership, table.ownership) && Objects.equals(ownerDisplayName, table.ownerDisplayName) && Objects.equals(createdBy, table.createdBy) && Objects.equals(createdAt, table.createdAt) && Objects.equals(lastEditBy, table.lastEditBy) && Objects.equals(lastEditAt, table.lastEditAt) && Objects.equals(onSharePermission, table.onSharePermission) && Objects.equals(currentRow, table.currentRow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, description, emoji, ownership, ownerDisplayName, createdBy, createdAt, lastEditBy, lastEditAt, isShared, onSharePermission, currentRow);
    }
}
