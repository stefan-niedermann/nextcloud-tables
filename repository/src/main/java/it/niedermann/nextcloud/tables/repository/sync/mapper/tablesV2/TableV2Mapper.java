package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.OnSharePermission;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.OnSharePermissionV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.TableV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class TableV2Mapper implements Mapper<TableV2Dto, Table> {

    private final Mapper<OnSharePermissionV2Dto, OnSharePermission> onSharePermissionMapper;

    public TableV2Mapper() {
        this(new OnSharePermissionV2Mapper());
    }

    private TableV2Mapper(@NonNull Mapper<OnSharePermissionV2Dto, OnSharePermission> onSharePermissionMapper) {
        this.onSharePermissionMapper = onSharePermissionMapper;
    }

    @NonNull
    @Override
    public TableV2Dto toDto(@NonNull Table entity) {
        final var onSharePermission = Optional
                .of(entity.getOnSharePermission())
                .map(onSharePermissionMapper::toDto)
                .orElse(null);
        return new TableV2Dto(
                entity.getRemoteId(),
                entity.getTitle(),
                entity.getEmoji(),
                entity.getDescription(),
                entity.getOwnership(),
                entity.getOwnerDisplayName(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getLastEditBy(),
                entity.getLastEditAt(),
                entity.isShared(),
                onSharePermission
        );
    }

    @NonNull
    @Override
    public Table toEntity(@NonNull TableV2Dto dto) {
        final var entity = new Table();
        entity.setRemoteId(dto.remoteId());
        entity.setTitle(dto.title());
        entity.setEmoji(dto.emoji());
        entity.setDescription(dto.description());
        entity.setOwnership(dto.ownership());
        entity.setOwnerDisplayName(dto.ownerDisplayName());
        entity.setCreatedBy(dto.createdBy());
        entity.setCreatedAt(dto.createdAt());
        entity.setLastEditBy(dto.lastEditBy());
        entity.setLastEditAt(dto.lastEditAt());
        entity.setShared(Boolean.TRUE.equals(dto.isShared()));
        final var onSharePermission = dto.onSharePermission();
        if (onSharePermission != null) {
            entity.setOnSharePermission(onSharePermissionMapper.toEntity(onSharePermission));
        }
        return entity;
    }
}
