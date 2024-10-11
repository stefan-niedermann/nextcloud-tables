package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;

public abstract class DataV1Mapper {

    @NonNull
    public abstract JsonElement toRemoteValue(@NonNull FullData entity,
                                              @NonNull EDataType dataType,
                                              @NonNull TablesVersion version);

    @NonNull
    public abstract FullData toData(@Nullable JsonElement dto,
                                    @Nullable Long columnRemoteId,
                                    @NonNull EDataType dataTypeAccordingToLocalColumn,
                                    @NonNull TablesVersion version);
}
