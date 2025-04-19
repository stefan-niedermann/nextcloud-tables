package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.entity.attributes.DateTimeAttributes;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.entity.attributes.UserGroupAttributes;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.SelectionOptionV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.UserGroupV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class ColumnV2Mapper implements Mapper<ColumnV2Dto, FullColumn> {

    private final Mapper<SelectionOptionV2Dto, SelectionOption> selectionOptionMapper;
    private final SelectionDefaultV2Mapper selectionDefaultMapper;
    private final Mapper<UserGroupV2Dto, UserGroup> userGroupMapper;

    public ColumnV2Mapper() {
        this(
                new SelectionOptionV2Mapper(),
                new SelectionDefaultV2Mapper(),
                new UserGroupV2Mapper()
        );
    }

    private ColumnV2Mapper(@NonNull Mapper<SelectionOptionV2Dto, SelectionOption> selectionOptionMapper,
                           @NonNull SelectionDefaultV2Mapper selectionDefaultMapper,
                           @NonNull Mapper<UserGroupV2Dto, UserGroup> userGroupMapper) {
        this.selectionOptionMapper = selectionOptionMapper;
        this.selectionDefaultMapper = selectionDefaultMapper;
        this.userGroupMapper = userGroupMapper;
    }

    @NonNull
    @Override
    public ColumnV2Dto toDto(@NonNull FullColumn entity) {

        final var selectionOptions = Optional
                .of(entity.getSelectionOptions())
                .map(selectionOptionMapper::toDtoList)
                .orElse(Collections.emptyList());

        final var selectionDefault = Optional.empty();
//                .ofNullable(entity.getDefaultSelectionOptions())
//                .map(selectionDefaultMapper::toDtoList)
//                .orElse(null);

        return new ColumnV2Dto(
                entity.getColumn().getRemoteId(),
                Objects.requireNonNullElse(entity.getColumn().getTitle(), ""),
                entity.getColumn().getCreatedAt(),
                entity.getColumn().getCreatedBy(),
                entity.getColumn().getLastEditBy(),
                entity.getColumn().getLastEditAt(),
                entity.getColumn().getDataType().getType(),
                entity.getColumn().getDataType().getSubType().orElse(""),
                entity.getColumn().isMandatory(),
                entity.getColumn().getDescription(),

                entity.getColumn().getDefaultValue().getDoubleValue(),
                entity.getColumn().getNumberAttributes().numberMin(),
                entity.getColumn().getNumberAttributes().numberMax(),
                entity.getColumn().getNumberAttributes().numberDecimals(),
                entity.getColumn().getNumberAttributes().numberPrefix(),
                entity.getColumn().getNumberAttributes().numberSuffix(),

                entity.getColumn().getDefaultValue().getStringValue(),
                entity.getColumn().getTextAttributes().textAllowedPattern(),
                entity.getColumn().getTextAttributes().textMaxLength(),

                selectionOptions,
                switch (entity.getColumn().getDataType()) {
                    case SELECTION, SELECTION_MULTI ->
                            selectionDefaultMapper.toDto(entity.getColumn().getDataType(), entity.getDefaultSelectionOptions());
                    case SELECTION_CHECK ->
                            new JsonPrimitive(Optional.ofNullable(entity.getColumn().getDefaultValue().getBooleanValue()).orElse(false));
                    default -> JsonNull.INSTANCE;
                },

                entity.getColumn().getDefaultValue().getStringValue(),

                userGroupMapper.toDtoList(filterUnknownTypes(entity.getDefaultUserGroups())),
                entity.getColumn().getUserGroupAttributes().usergroupMultipleItems(),
                entity.getColumn().getUserGroupAttributes().usergroupSelectUsers(),
                entity.getColumn().getUserGroupAttributes().usergroupSelectGroups(),
                entity.getColumn().getUserGroupAttributes().showUserStatus()
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
    public FullColumn toEntity(@NonNull ColumnV2Dto dto) {
        final var entity = new FullColumn();
        final var column = new Column();

        column.setRemoteId(dto.remoteId());
        column.setTitle(Objects.requireNonNullElse(dto.title(), ""));
        column.setCreatedAt(dto.createdAt());
        column.setCreatedBy(dto.createdBy());
        column.setLastEditBy(dto.lastEditBy());
        column.setLastEditAt(dto.lastEditAt());
        column.setDataType(EDataType.findByType(dto.type(), dto.subtype()));
        column.setMandatory(Boolean.TRUE.equals(dto.mandatory()));
        column.setDescription(dto.description());
        column.setNumberAttributes(new NumberAttributes(
                dto.numberMin(),
                dto.numberMax(),
                dto.numberDecimals(),
                dto.numberPrefix(),
                dto.numberSuffix()
        ));

        column.setTextAttributes(new TextAttributes(
                dto.textAllowedPattern(),
                dto.textMaxLength()
        ));
        column.setDateTimeAttributes(new DateTimeAttributes(
        ));
        column.setUserGroupAttributes(new UserGroupAttributes(
                Boolean.TRUE.equals(dto.usergroupMultipleItems()),
                Boolean.TRUE.equals(dto.usergroupSelectUsers()),
                Boolean.TRUE.equals(dto.usergroupSelectGroups()),
                Boolean.TRUE.equals(dto.showUserStatus())
        ));

        final var defaultValue = new Value();
        switch (column.getDataType()) {
            case NUMBER, NUMBER_PROGRESS, NUMBER_STARS ->
                    defaultValue.setDoubleValue(dto.numberDefault());
            case SELECTION_CHECK -> defaultValue.setBooleanValue(Optional
                    .ofNullable(dto.selectionDefault())
                    .map(jsonElement -> jsonElement.isJsonNull() ? null : jsonElement.getAsBoolean())
                    .orElse(false));
            case USERGROUP -> {
                final var defaultUserGroups = userGroupMapper.toEntityList(Optional.ofNullable(dto.usergroupDefault()).orElse(Collections.emptyList()));
                entity.setDefaultUserGroups(defaultUserGroups);
            }
            case SELECTION_MULTI, SELECTION -> {
                final var defaultSelectionOptions = selectionDefaultMapper.toEntity(Optional.ofNullable(dto.selectionDefault()).orElse(JsonNull.INSTANCE));
                entity.setDefaultSelectionOptions(defaultSelectionOptions);
            }
            default -> defaultValue.setStringValue(dto.textDefault());
        }
        column.setDefaultValue(defaultValue);

        final var selectionOptions = Optional
                .ofNullable(dto.selectionOptions())
                .map(selectionOptionMapper::toEntityList)
                .orElse(Collections.emptyList());

        entity.setSelectionOptions(selectionOptions);
        entity.setColumn(column);

        return entity;
    }
}
