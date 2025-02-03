package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.io.Serializable;
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
        },
        indices = {
                @Index(value = {"accountId", "remoteId"}, unique = true),
        }
)
public class SearchProvider extends AbstractAccountRelatedEntity implements Serializable, Comparable<SearchProvider> {

    /// Unique per accountId
    /// For example `tables-search-tables`
    @NonNull
    private String remoteId = "";
    /// For example `tables`
    @Nullable
    private String appId;
    /// For example `Nextcloud tables`
    @Nullable
    private String name;
    /// For example `/apps/tables/img/app.svg`
    @Nullable
    private String icon;
    private int order;
    private boolean inAppSearch;

    public SearchProvider() {
        // Default constructor
    }

    @Ignore
    public SearchProvider(@NonNull SearchProvider searchProvider) {
        super(searchProvider);
        remoteId = searchProvider.remoteId;
        appId = searchProvider.appId;
        name = searchProvider.name;
        icon = searchProvider.icon;
        order = searchProvider.order;
        inAppSearch = searchProvider.inAppSearch;
    }

    @NonNull
    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(@NonNull String remoteId) {
        this.remoteId = remoteId;
    }

    @Nullable
    public String getAppId() {
        return appId;
    }

    public void setAppId(@Nullable String appId) {
        this.appId = appId;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getIcon() {
        return icon;
    }

    public void setIcon(@Nullable String icon) {
        this.icon = icon;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isInAppSearch() {
        return inAppSearch;
    }

    public void setInAppSearch(boolean inAppSearch) {
        this.inAppSearch = inAppSearch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SearchProvider that = (SearchProvider) o;
        return order == that.order && inAppSearch == that.inAppSearch && Objects.equals(remoteId, that.remoteId) && Objects.equals(appId, that.appId) && Objects.equals(name, that.name) && Objects.equals(icon, that.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId, appId, name, icon, order, inAppSearch);
    }

    @Override
    public int compareTo(SearchProvider o) {
        if (o == null) {
            return 1;
        }

        return Integer.compare(order, o.order);
    }
}
