package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.SelectionOptionV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class SelectionOptionV2Mapper implements Mapper<SelectionOptionV2Dto, SelectionOption> {
    @NonNull
    @Override
    public SelectionOptionV2Dto toDto(@NonNull SelectionOption entity) {
        return new SelectionOptionV2Dto(
                entity.getRemoteId(),
                entity.getLabel()
        );
    }

    @NonNull
    @Override
    public SelectionOption toEntity(@NonNull SelectionOptionV2Dto dto) {
        final var entity = new SelectionOption();
        entity.setRemoteId(dto.remoteId());
        entity.setLabel(dto.label());
        return entity;
    }
}
