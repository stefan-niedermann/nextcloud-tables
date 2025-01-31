package it.niedermann.nextcloud.tables.remote.ocs.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/// [Documentation](https://docs.nextcloud.com/server/latest/developer_manual/client_apis/OCS/ocs-api-overview.html#auto-complete-and-user-search)
public record OcsAutocompleteResult(
        @NonNull String id,
        @NonNull String label,
        @Nullable String icon,
        @NonNull OcsAutocompleteSource source,
        @Nullable String subline,
        @Nullable String shareWithDisplayNameUnique
) implements Serializable {

    public enum OcsAutocompleteSource {
        @SerializedName(value = "0", alternate = "users")
        USERS(0),
        @SerializedName(value = "1", alternate = "groups")
        GROUPS(1),
        ;

        public final int shareType;

        OcsAutocompleteSource(int shareType) {
            this.shareType = shareType;
        }
    }
}
