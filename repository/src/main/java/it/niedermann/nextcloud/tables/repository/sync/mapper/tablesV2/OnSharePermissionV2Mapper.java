package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.OnSharePermission;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.OnSharePermissionV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class OnSharePermissionV2Mapper implements Mapper<OnSharePermissionV2Dto, OnSharePermission> {
    @NonNull
    @Override
    public OnSharePermissionV2Dto toDto(@NonNull OnSharePermission entity) {
        return new OnSharePermissionV2Dto(
                Boolean.TRUE.equals(entity.isRead()),
                Boolean.TRUE.equals(entity.isCreate()),
                Boolean.TRUE.equals(entity.isUpdate()),
                Boolean.TRUE.equals(entity.isDelete()),
                Boolean.TRUE.equals(entity.isManage())
        );
    }

    @NonNull
    @Override
    public OnSharePermission toEntity(@NonNull OnSharePermissionV2Dto dto) {
        final var entity = new OnSharePermission();
        entity.setRead(Boolean.TRUE.equals(dto.read()));
        entity.setCreate(Boolean.TRUE.equals(dto.create()));
        entity.setUpdate(Boolean.TRUE.equals(dto.update()));
        entity.setDelete(Boolean.TRUE.equals(dto.delete()));
        entity.setManage(Boolean.TRUE.equals(dto.manage()));
        return entity;
    }
}
