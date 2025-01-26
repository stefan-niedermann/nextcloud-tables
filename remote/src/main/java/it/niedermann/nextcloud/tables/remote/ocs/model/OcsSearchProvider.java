package it.niedermann.nextcloud.tables.remote.ocs.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/// @see <a href="https://docs.nextcloud.com/server/latest/developer_manual/digging_deeper/search.html">Source</a>
public record OcsSearchProvider(
        @SerializedName("id")
        @Nullable String remoteId,
        @Nullable String appId,
        @Nullable String name,
        @Nullable String icon,
        int order,
        boolean inAppSearch
) implements Serializable {
}
