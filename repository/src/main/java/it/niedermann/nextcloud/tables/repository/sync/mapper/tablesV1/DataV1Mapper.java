package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.DataV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class DataV1Mapper implements Mapper<DataV1Dto, Data> {
    @Override
    @NonNull
    public DataV1Dto toDto(@NonNull Data entity) {
        return new DataV1Dto(
                entity.getRowId(),
                entity.getValue()
        );
    }

    @Override
    @NonNull
    public Data toEntity(@NonNull DataV1Dto dto) {
        final var data = new Data();
        data.setRemoteColumnId(dto.remoteColumnId());
        data.setValue(dto.value());
        return data;
    }
}