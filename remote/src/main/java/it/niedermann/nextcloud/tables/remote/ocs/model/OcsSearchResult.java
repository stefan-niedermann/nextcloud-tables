package it.niedermann.nextcloud.tables.remote.ocs.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public record OcsSearchResult(
        @Nullable String name,
        boolean isPaginated,
        @NonNull List<OcsSearchResultEntry> entries,
        @Nullable Integer cursor
) implements Serializable {
}
