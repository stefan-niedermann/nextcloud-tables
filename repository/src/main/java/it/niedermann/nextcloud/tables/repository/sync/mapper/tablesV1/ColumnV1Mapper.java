package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.SelectionOptionV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class ColumnV1Mapper implements Mapper<ColumnV1Dto, Column> {

    private final Mapper<SelectionOptionV1Dto, SelectionOption> selectionOptionMapper;
    private final Mapper<JsonElement, SelectionDefault> selectionDefaultMapper;

    public ColumnV1Mapper() {
        this(
                new SelectionOptionV1Mapper(),
                new SelectionDefaultV1Mapper()
        );
    }

    private ColumnV1Mapper(@NonNull SelectionOptionV1Mapper selectionOptionMapper,
                           Mapper<JsonElement, SelectionDefault> selectionDefaultMapper) {
        this.selectionOptionMapper = selectionOptionMapper;
        this.selectionDefaultMapper = selectionDefaultMapper;
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
                entity.getUsergroupDefault(),
                false, false, false, false
//                column.getUsergroupMultipleItems(),
//                column.getUsergroupSelectUsers(),
//                column.getUsergroupSelectGroups(),
//                column.getShowUserStatus()
        );
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
        final var selectionOptions = dto.selectionOptions();
        if (selectionOptions != null) {
            column.setSelectionOptions(selectionOptionMapper.toEntityList(dto.selectionOptions()));
        }
        final var selectionDefault = dto.selectionDefault();
        if (selectionDefault != null) {
            column.setSelectionDefault(selectionDefaultMapper.toEntity(selectionDefault));
        }
        column.setDatetimeDefault(dto.datetimeDefault());
        column.setUsergroupDefault(dto.usergroupDefault());
        column.setUsergroupMultipleItems(Boolean.TRUE.equals(dto.usergroupMultipleItems()));
        column.setUsergroupSelectUsers(Boolean.TRUE.equals(dto.usergroupSelectUsers()));
        column.setUsergroupSelectGroups(Boolean.TRUE.equals(dto.usergroupSelectGroups()));
        column.setShowUserStatus(Boolean.TRUE.equals(dto.showUserStatus()));
        return column;
    }
}
