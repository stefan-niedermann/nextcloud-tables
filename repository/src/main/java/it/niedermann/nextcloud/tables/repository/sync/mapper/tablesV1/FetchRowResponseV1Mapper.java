package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.FetchRowResponseV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.TypeRemoteMapperServiceRegistry;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public class FetchRowResponseV1Mapper {

    private static final String TAG = FetchRowResponseV1Mapper.class.getSimpleName();

    private final TypeRemoteMapperServiceRegistry registry = new TypeRemoteMapperServiceRegistry();

    public FullRow toEntity(long accountId,
                            @NonNull FetchRowResponseV1Dto dto,
                            @NonNull Map<Long, FullColumn> remoteIdToFullColumns,
                            @NonNull Map<Long, List<SelectionOption>> columnRemoteIdToSelectionColumns,
                            @NonNull TablesVersion tablesVersion) {
        final var fullRow = new FullRow();

        final var dataDtoList = Optional.ofNullable(dto.data());
        final var fullDataList = dataDtoList
                .map(List::size)
                .map(ArrayList<FullData>::new)
                .orElseGet(ArrayList::new);

        dataDtoList.ifPresent(list -> {
            for (final var dataDto : list) {
                final var optionalColumn = Optional
                        .ofNullable(dataDto.remoteColumnId())
                        .map(remoteIdToFullColumns::get);

                if (optionalColumn.isEmpty()) {
                    if (FeatureToggle.STRICT_MODE.enabled) {
                        throw new IllegalStateException("Unknown remote column id " + dataDto.remoteColumnId() + ". Columns must be synchronized before rows.");
                    } else {
                        Log.w(TAG, "Unknown remote column id " + dataDto.remoteColumnId() + " in dataDtoList " + list);
                        continue;
                    }
                }

                final var fullColumn = optionalColumn.get();
                final var column = fullColumn.getColumn();
                final var service = registry.getService(column.getDataType());
                final var fullData = service.toFullData(accountId, dataDto.value(), fullColumn, tablesVersion);

                Optional.ofNullable(column.getRemoteId()).ifPresent(fullData.getData()::setRemoteColumnId);

                fullDataList.add(fullData);
            }
        });

        fullRow.setFullData(fullDataList);

        final var row = new Row();
        row.setAccountId(accountId);
        row.setRemoteId(dto.remoteId());
        row.setCreatedAt(dto.createdAt());
        row.setCreatedBy(dto.createdBy());
        row.setLastEditAt(dto.lastEditAt());
        row.setLastEditBy(dto.lastEditBy());
        fullRow.setRow(row);

        return fullRow;
    }

    @NonNull
    public JsonElement toJsonElement(@NonNull TablesVersion version,
                                     @NonNull Collection<FullData> fullDataSet) {
        final var properties = new JsonObject();

        for (final var fullData : fullDataSet) {
            final var service = registry.getService(fullData.getDataType());
            properties.add(
                    String.valueOf(fullData.getData().getRemoteColumnId()),
                    service.toRemoteValue(fullData, fullData.getDataType(), version)
            );
        }

        return properties;
    }
}
