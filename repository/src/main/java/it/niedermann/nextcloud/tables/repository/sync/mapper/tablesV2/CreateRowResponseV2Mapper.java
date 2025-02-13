package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateRowResponseV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateRowV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.TypeRemoteV1MapperServiceRegistry;
import it.niedermann.nextcloud.tables.repository.sync.treesync.TreeSyncExceptionWithContext;

public class CreateRowResponseV2Mapper {

    private final TypeRemoteV1MapperServiceRegistry registry = new TypeRemoteV1MapperServiceRegistry();

    public FullRow toEntity(long accountId,
                            @NonNull CreateRowResponseV2Dto dto,
                            @NonNull Map<Long, FullColumn> remoteIdToFullColumns,
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
                    continue;
                }

                final var fullColumn = optionalColumn.get();
                final var column = fullColumn.getColumn();
                final var service = registry.getService(column.getDataType());

                try {
                    final var fullData = service.toFullData(accountId, dataDto.value(), fullColumn, tablesVersion);
                    fullDataList.add(fullData);
                } catch (Throwable throwable) {
                    throw new TreeSyncExceptionWithContext(throwable)
                            .provide(accountId, fullColumn, tablesVersion)
                            .provide("Value", dataDto.value());
                }
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
    public CreateRowV2Dto toCreateRowV2Dto(@NonNull TablesVersion version,
                                           @NonNull Collection<FullData> fullDataSet) {
        final var data = new HashMap<Long, JsonElement>(fullDataSet.size());

        for (final var fullData : fullDataSet) {
            final var service = registry.getService(fullData.getDataType());
            try {
                data.put(
                        fullData.getData().getRemoteColumnId(),
                        service.toRemoteValue(fullData, fullData.getDataType(), version));
            } catch (Throwable throwable) {
                throw new TreeSyncExceptionWithContext(throwable).provide(fullData, version);
            }
        }

        return new CreateRowV2Dto(data);
    }
}
