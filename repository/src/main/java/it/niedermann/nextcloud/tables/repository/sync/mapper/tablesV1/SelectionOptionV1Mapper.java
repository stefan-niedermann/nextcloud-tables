package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.SelectionOptionV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class SelectionOptionV1Mapper implements Mapper<SelectionOptionV1Dto, SelectionOption> {
    @NonNull
    @Override
    public SelectionOptionV1Dto toDto(@NonNull SelectionOption entity) {
        return new SelectionOptionV1Dto(
                entity.getRemoteId(),
                entity.getLabel()
        );
    }

    @NonNull
    @Override
    public SelectionOption toEntity(@NonNull SelectionOptionV1Dto dto) {
        final var entity = new SelectionOption();
        entity.setRemoteId(dto.remoteId());
        entity.setLabel(dto.label());
        return entity;
    }
}
