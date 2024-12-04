package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.FetchRowResponseV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateRowV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.TypeRemoteMapperServiceRegistry;

public class CreateRowResponseV2Mapper {

    private final TypeRemoteMapperServiceRegistry registry = new TypeRemoteMapperServiceRegistry();

    public FullRow toEntity(long accountId,
                            @NonNull FetchRowResponseV1Dto dto,
                            @NonNull Map<Long, Column> remoteIdToColumns,
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
                        .map(remoteIdToColumns::get);

                if (optionalColumn.isEmpty()) {
                    continue;
                }

                final var column = optionalColumn.get();
                final var service = registry.getService(column.getDataType());
                final var fullData = service.toData(dataDto.value(), column.getRemoteId(), column.getDataType(), tablesVersion);

                fullData.getData().setAccountId(accountId);
                fullData.getData().setColumnId(column.getId());
                fullData.setDataType(column.getDataType());

                for (final var selectionOption : fullData.getSelectionOptions()) {
                    selectionOption.setAccountId(accountId);
                    selectionOption.setColumnId(column.getId());
                }

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
    public CreateRowV2Dto toCreateRowV2Dto(@NonNull TablesVersion version,
                                           @NonNull Collection<FullData> fullDataSet) {

        final var data = new JsonObject();

        for (final var fullData : fullDataSet) {
            final var service = registry.getService(fullData.getDataType());
            data.add(
                    String.valueOf(fullData.getData().getRemoteColumnId()),
                    service.toRemoteValue(fullData, fullData.getDataType(), version)
            );
        }

        return new CreateRowV2Dto(data);
    }
}
