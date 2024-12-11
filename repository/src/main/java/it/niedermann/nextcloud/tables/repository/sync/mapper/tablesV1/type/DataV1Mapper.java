package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;

public abstract class DataV1Mapper {

    @NonNull
    public abstract JsonElement toRemoteValue(@NonNull FullData entity,
                                              @NonNull EDataType dataType,
                                              @NonNull TablesVersion version);

    @NonNull
    public final FullData toFullData(long accountId,
                                     @Nullable JsonElement value,
                                     @NonNull FullColumn fullColumn,
                                     @NonNull TablesVersion version) {
        final var fullData = new FullData();
        final var data = new Data();

        fullData.setDataType(fullColumn.getColumn().getDataType());
        fullData.setData(data);

        data.setAccountId(accountId);
        data.setColumnId(fullColumn.getColumn().getId());

        Optional.ofNullable(fullColumn.getColumn().getRemoteId())
                .ifPresent(data::setRemoteColumnId);

        this.toFullData(fullData, value, fullColumn, version);

        return fullData;
    }

    /// Formats and writes the given `value` into `fullData`.
    protected abstract void toFullData(@NonNull FullData fullData,
                                       @Nullable JsonElement value,
                                       @NonNull FullColumn fullColumn,
                                       @NonNull TablesVersion version);
}
