package it.niedermann.nextcloud.tables.database.entity;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;


@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Data.class,
                        parentColumns = "id",
                        childColumns = "dataId",
                        onDelete = ForeignKey.CASCADE
                ),
                // Mimic the behavior of the Tables server app: When a provider is no longer available, we keep the link as a plain URL value
                @ForeignKey(
                        entity = SearchProvider.class,
                        parentColumns = "id",
                        childColumns = "providerId",
                        onDelete = ForeignKey.SET_NULL
                ),
        },
        indices = {
                @Index(value = "providerId"),
                @Index(value = "dataId", unique = true),
                @Index(value = {"dataId", "providerId"}, unique = true),
        }
)
public class LinkValue implements Serializable {

    @PrimaryKey
    private long dataId;

    /// May be `null` in case of plain `url` values
    @Nullable
    private Long providerId;

    /// For example: `https://www.google.de`
    ///
    /// @noinspection JavadocLinkAsPlainText
    @Nullable
    private String title;

    /// For example: `Url`
    ///
    /// @noinspection SpellCheckingInspection
    @Nullable
    private String subline;

    /// For example: `https://www.google.de`
    ///
    /// @noinspection JavadocLinkAsPlainText, NotNullFieldNotInitialized
    @NonNull
    private Uri value;

    public LinkValue() {
        // Default constructor
    }

    @Ignore
    public LinkValue(@NonNull LinkValue linkValue) {
        this.dataId = linkValue.dataId;
        this.subline = linkValue.subline;
        this.title = linkValue.title;
        this.value = linkValue.value;
        this.providerId = linkValue.providerId;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    @Nullable
    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(@Nullable Long providerId) {
        this.providerId = providerId;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    public String getSubline() {
        return subline;
    }

    public void setSubline(@Nullable String subline) {
        this.subline = subline;
    }

    @NonNull
    public Uri getValue() {
        return value;
    }

    public void setValue(@NonNull Uri value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LinkValue linkValue = (LinkValue) o;
        return dataId == linkValue.dataId && Objects.equals(providerId, linkValue.providerId) && Objects.equals(title, linkValue.title) && Objects.equals(subline, linkValue.subline) && Objects.equals(value, linkValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataId, providerId, title, subline, value);
    }
}
