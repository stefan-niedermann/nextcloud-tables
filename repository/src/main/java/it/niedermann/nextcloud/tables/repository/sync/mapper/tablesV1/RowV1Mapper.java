package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.DataV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.RowV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class RowV1Mapper implements Mapper<RowV1Dto, Row> {

    private final Mapper<DataV1Dto, Data> dataMapper;

    public RowV1Mapper() {
        this(new DataV1Mapper());
    }

    private RowV1Mapper(@NonNull Mapper<DataV1Dto, Data> dataMapper) {
        this.dataMapper = dataMapper;
    }

    @NonNull
    @Override
    public RowV1Dto toDto(@NonNull Row row) {
        final var data = Optional
                .ofNullable(row.getData())
                .map(Arrays::asList)
                .map(dataMapper::toDtoList)
                .map(list -> list.toArray(DataV1Dto[]::new))
                .orElse(new DataV1Dto[]{});
        return new RowV1Dto(
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
    public Row toEntity(@NonNull RowV1Dto dto) {
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