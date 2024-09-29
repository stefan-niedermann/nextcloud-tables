package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.SelectionOptionV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.UserGroupV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class ColumnV2Mapper implements Mapper<ColumnV2Dto, Column> {

    private final Mapper<SelectionOptionV2Dto, SelectionOption> selectionOptionMapper;
    private final Mapper<JsonElement, SelectionDefault> selectionDefaultMapper;
    private final Mapper<UserGroupV2Dto, UserGroup> userGroupMapper;

    public ColumnV2Mapper() {
        this(
                new SelectionOptionV2Mapper(),
                new SelectionDefaultV2Mapper(),
                new UserGroupV2Mapper()
        );
    }

    private ColumnV2Mapper(@NonNull Mapper<SelectionOptionV2Dto, SelectionOption> selectionOptionMapper,
                           @NonNull Mapper<JsonElement, SelectionDefault> selectionDefaultMapper,
                           @NonNull Mapper<UserGroupV2Dto, UserGroup> userGroupMapper) {
        this.selectionOptionMapper = selectionOptionMapper;
        this.selectionDefaultMapper = selectionDefaultMapper;
        this.userGroupMapper = userGroupMapper;
    }

    @NonNull
    @Override
    public ColumnV2Dto toDto(@NonNull Column entity) {
        final List<SelectionOptionV2Dto> selectionOptions = Optional
                .ofNullable(entity.getSelectionOptions())
                .map(selectionOptionMapper::toDtoList)
                .orElse(Collections.emptyList());
        final var selectionDefault = Optional
                .ofNullable(entity.getSelectionDefault())
                .map(selectionDefaultMapper::toDto)
                .orElse(null);
        return new ColumnV2Dto(
                entity.getRemoteId(),
                Objects.requireNonNullElse(entity.getTitle(), ""),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getLastEditBy(),
                entity.getLastEditAt(),
                entity.getType(),
                entity.getSubtype(),
                entity.isMandatory(),
                entity.getDescription(),
                entity.getNumberDefault(),
                entity.getNumberMin(),
                entity.getNumberMax(),
                entity.getNumberDecimals(),
                entity.getNumberPrefix(),
                entity.getNumberSuffix(),
                entity.getTextDefault(),
                entity.getTextAllowedPattern(),
                entity.getTextMaxLength(),
                selectionOptions,
                selectionDefault,
                entity.getDatetimeDefault(),
                userGroupMapper.toDtoList(filterUnknownTypes(entity.getUsergroupDefault())),
                entity.isUsergroupMultipleItems(),
                entity.isUsergroupSelectUsers(),
                entity.isUsergroupSelectGroups(),
                entity.isShowUserStatus()
        );
    }

    @NonNull
    private Collection<UserGroup> filterUnknownTypes(@Nullable Collection<UserGroup> userGroups) {
        return Optional.ofNullable(userGroups)
                .orElse(Collections.emptyList())
                .stream()
                .filter(userGroup -> userGroup.getType() != EUserGroupType.UNKNOWN)
                .collect(Collectors.toUnmodifiableList());
    }

    @NonNull
    @Override
    public Column toEntity(@NonNull ColumnV2Dto dto) {
        final var entity = new Column();
        entity.setRemoteId(dto.remoteId());
        entity.setTitle(Objects.requireNonNullElse(dto.title(), ""));
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBy(dto.createdBy());
        entity.setLastEditBy(dto.lastEditBy());
        entity.setLastEditAt(dto.lastEditAt());
        entity.setType(dto.type());
        entity.setSubtype(dto.subtype());
        entity.setMandatory(Boolean.TRUE.equals(dto.mandatory()));
        entity.setDescription(dto.description());
        entity.setNumberDefault(dto.numberDefault());
        entity.setNumberMin(dto.numberMin());
        entity.setNumberMax(dto.numberMax());
        entity.setNumberDecimals(dto.numberDecimals());
        entity.setNumberPrefix(dto.numberPrefix());
        entity.setNumberSuffix(dto.numberSuffix());
        entity.setTextDefault(dto.textDefault());
        entity.setTextAllowedPattern(dto.textAllowedPattern());
        entity.setTextMaxLength(dto.textMaxLength());
        entity.setSelectionOptions(selectionOptionMapper.toEntityList(Optional.ofNullable(dto.selectionOptions()).orElse(Collections.emptyList())));
        final var selectionDefault = dto.selectionDefault();
        if (selectionDefault != null) {
            entity.setSelectionDefault(selectionDefaultMapper.toEntity(selectionDefault));
        }
        entity.setDatetimeDefault(dto.datetimeDefault());
        entity.setUsergroupDefault(userGroupMapper.toEntityList(Optional.ofNullable(dto.usergroupDefault()).orElse(Collections.emptyList())));
        entity.setUsergroupMultipleItems(Boolean.TRUE.equals(dto.usergroupMultipleItems()));
        entity.setUsergroupSelectUsers(Boolean.TRUE.equals(dto.usergroupSelectUsers()));
        entity.setUsergroupSelectGroups(Boolean.TRUE.equals(dto.usergroupSelectGroups()));
        entity.setShowUserStatus(Boolean.TRUE.equals(dto.showUserStatus()));
        return entity;
    }
}
