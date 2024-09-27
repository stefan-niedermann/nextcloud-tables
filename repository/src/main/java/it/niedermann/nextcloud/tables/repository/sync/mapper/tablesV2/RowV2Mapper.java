package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.DataV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.RowV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class RowV2Mapper implements Mapper<RowV2Dto, Row> {

    private final Mapper<DataV2Dto, Data> dataMapper;

    public RowV2Mapper() {
        this(new DataV2Mapper());
    }

    private RowV2Mapper(@NonNull Mapper<DataV2Dto, Data> dataMapper) {
        this.dataMapper = dataMapper;
    }

    @NonNull
    @Override
    public RowV2Dto toDto(@NonNull Row row) {
        final var data = Optional
                .ofNullable(row.getData())
                .map(Arrays::asList)
                .map(dataMapper::toDtoList)
                .map(list -> list.toArray(DataV2Dto[]::new))
                .orElse(new DataV2Dto[]{});
        return new RowV2Dto(
                row.getRemoteId(),
                row.getCreatedBy(),
                row.getCreatedAt(),
                row.getLastEditBy(),
                row.getLastEditAt(),
                data
        );
    }

    @Override
    @NonNull
    public Row toEntity(@NonNull RowV2Dto dto) {
        final var row = new Row();
        row.setRemoteId(dto.remoteId());
        row.setCreatedBy(dto.createdBy());
        row.setCreatedAt(dto.createdAt());
        row.setLastEditBy(dto.lastEditBy());
        row.setLastEditAt(dto.lastEditAt());
        final var data = dto.data();
        if (data != null) {
            row.setData(dataMapper.toEntityList(Arrays.asList(data)).toArray(Data[]::new));
        }
        return row;
    }
}