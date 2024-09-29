package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

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
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.SelectionOptionV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UserGroupV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class ColumnV1Mapper implements Mapper<ColumnV1Dto, Column> {

    private final Mapper<SelectionOptionV1Dto, SelectionOption> selectionOptionMapper;
    private final Mapper<JsonElement, SelectionDefault> selectionDefaultMapper;
    private final Mapper<UserGroupV1Dto, UserGroup> userGroupMapper;

    public ColumnV1Mapper() {
        this(
                new SelectionOptionV1Mapper(),
                new SelectionDefaultV1Mapper(),
                new UserGroupV1Mapper()
        );
    }

    private ColumnV1Mapper(@NonNull Mapper<SelectionOptionV1Dto, SelectionOption> selectionOptionMapper,
                           @NonNull Mapper<JsonElement, SelectionDefault> selectionDefaultMapper,
                           @NonNull Mapper<UserGroupV1Dto, UserGroup> userGroupMapper) {
        this.selectionOptionMapper = selectionOptionMapper;
        this.selectionDefaultMapper = selectionDefaultMapper;
        this.userGroupMapper = userGroupMapper;
    }

    @NonNull
    @Override
    public ColumnV1Dto toDto(@NonNull Column entity) {
        final List<SelectionOptionV1Dto> selectionOptions = Optional
                .ofNullable(entity.getSelectionOptions())
                .map(selectionOptionMapper::toDtoList)
                .orElse(Collections.emptyList());
        final var selectionDefault = Optional
                .ofNullable(entity.getSelectionDefault())
                .map(selectionDefaultMapper::toDto)
                .orElse(null);
        return new ColumnV1Dto(
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
    public Column toEntity(@NonNull ColumnV1Dto dto) {
        final var column = new Column();
        column.setRemoteId(dto.remoteId());
        column.setTitle(Objects.requireNonNullElse(dto.title(), ""));
        column.setCreatedAt(dto.createdAt());
        column.setCreatedBy(dto.createdBy());
        column.setLastEditBy(dto.lastEditBy());
        column.setLastEditAt(dto.lastEditAt());
        column.setType(dto.type());
        column.setSubtype(dto.subtype());
        column.setMandatory(Boolean.TRUE.equals(dto.mandatory()));
        column.setDescription(dto.description());
        column.setNumberDefault(dto.numberDefault());
        column.setNumberMin(dto.numberMin());
        column.setNumberMax(dto.numberMax());
        column.setNumberDecimals(dto.numberDecimals());
        column.setNumberPrefix(dto.numberPrefix());
        column.setNumberSuffix(dto.numberSuffix());
        column.setTextDefault(dto.textDefault());
        column.setTextAllowedPattern(dto.textAllowedPattern());
        column.setTextMaxLength(dto.textMaxLength());
        column.setSelectionOptions(selectionOptionMapper.toEntityList(Optional.ofNullable(dto.selectionOptions()).orElse(Collections.emptyList())));
        final var selectionDefault = dto.selectionDefault();
        if (selectionDefault != null) {
            column.setSelectionDefault(selectionDefaultMapper.toEntity(selectionDefault));
        }
        column.setDatetimeDefault(dto.datetimeDefault());
        column.setUsergroupDefault(userGroupMapper.toEntityList(Optional.ofNullable(dto.usergroupDefault()).orElse(Collections.emptyList())));
        column.setUsergroupMultipleItems(Boolean.TRUE.equals(dto.usergroupMultipleItems()));
        column.setUsergroupSelectUsers(Boolean.TRUE.equals(dto.usergroupSelectUsers()));
        column.setUsergroupSelectGroups(Boolean.TRUE.equals(dto.usergroupSelectGroups()));
        column.setShowUserStatus(Boolean.TRUE.equals(dto.showUserStatus()));
        return column;
    }
}
