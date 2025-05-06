package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.util.Locale;

import it.niedermann.nextcloud.tables.remote.shared.model.RemoteDto;

public record TableV2Dto(
        @SerializedName("id")
        @Nullable Long remoteId,
        @NonNull String title,
        @NonNull String emoji,
        @Nullable String description,
        @Nullable String ownership,
        @Nullable String ownerDisplayName,
        @Nullable String createdBy,
        @Nullable Instant createdAt,
        @Nullable String lastEditBy,
        @Nullable Instant lastEditAt,
        @Nullable Boolean isShared,
        @Nullable OnSharePermissionV2Dto onSharePermissions
) implements Serializable, RemoteDto {

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s %s", emoji(), title()).trim();
    }
}
