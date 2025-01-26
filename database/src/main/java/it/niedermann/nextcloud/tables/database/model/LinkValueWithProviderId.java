package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.LinkValue;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;

public class LinkValueWithProviderId implements Serializable {

    @NonNull
    @Embedded
    private LinkValue linkValue;

    @Nullable
    @Relation(
            parentColumn = "providerId",
            entity = SearchProvider.class,
            entityColumn = "id",
            projection = "remoteId"
    )
    private String providerId;

    public LinkValueWithProviderId() {
        this.linkValue = new LinkValue();
        this.providerId = null;
    }

    @Ignore
    public LinkValueWithProviderId(@NonNull LinkValue linkValue,
                                   @Nullable String providerId) {
        this.linkValue = linkValue;
        this.providerId = providerId;
    }

    @Ignore
    public LinkValueWithProviderId(@NonNull LinkValueWithProviderId linkValueWithProviderId) {
        this.linkValue = linkValueWithProviderId.linkValue;
        this.providerId = linkValueWithProviderId.providerId;
    }

    @NonNull
    public LinkValue getLinkValue() {
        return linkValue;
    }

    public void setLinkValue(@NonNull LinkValue linkValue) {
        this.linkValue = linkValue;
    }

    @Nullable
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(@Nullable String providerId) {
        this.providerId = providerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkValueWithProviderId that = (LinkValueWithProviderId) o;
        return Objects.equals(linkValue, that.linkValue) && Objects.equals(providerId, that.providerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkValue, providerId);
    }
}
