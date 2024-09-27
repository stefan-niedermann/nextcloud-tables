package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.DataV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class DataV2Mapper implements Mapper<DataV2Dto, Data> {
    @Override
    @NonNull
    public DataV2Dto toDto(@NonNull Data entity) {
        return new DataV2Dto(
                entity.getRemoteColumnId(),
                entity.getValue()
        );
    }

    @Override
    @NonNull
    public Data toEntity(@NonNull DataV2Dto dto) {
        final var data = new Data();
        data.setRemoteColumnId(dto.remoteColumnId());
        data.setValue(dto.value());
        return data;
    }
}