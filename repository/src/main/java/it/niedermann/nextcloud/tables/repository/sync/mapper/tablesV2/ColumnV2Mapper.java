package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import static java.util.function.Predicate.not;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;

public class ColumnV2Mapper implements RemoteMapper<ColumnV2Dto, FullColumn> {

    private final RemoteMapper<SelectionOptionV2Dto, SelectionOption> selectionOptionMapper;
    private final SelectionDefaultV2Mapper selectionDefaultMapper;
    private final RemoteMapper<UserGroupV2Dto, UserGroup> userGroupMapper;

    public ColumnV2Mapper() {
        this(
                SelectionOptionV2Mapper.INSTANCE,
                new SelectionDefaultV2Mapper(),
                UserGroupV2Mapper.INSTANCE
        );
    }

    private ColumnV2Mapper(@NonNull RemoteMapper<SelectionOptionV2Dto, SelectionOption> selectionOptionMapper,
                           @NonNull SelectionDefaultV2Mapper selectionDefaultMapper,
                           @NonNull RemoteMapper<UserGroupV2Dto, UserGroup> userGroupMapper) {
        this.selectionOptionMapper = selectionOptionMapper;
        this.selectionDefaultMapper = selectionDefaultMapper;
        this.userGroupMapper = userGroupMapper;
    }

    @NonNull
    @Override
    public ColumnV2Dto toDto(@NonNull FullColumn entity) {
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

                Optional
                        .of(entity.getSelectionOptions())
                        .map(selectionOptionMapper::toDtoList)
                        .filter(not(List::isEmpty))
                        .orElse(null),
                switch (entity.getColumn().getDataType()) {
                    case SELECTION, SELECTION_MULTI ->
                            selectionDefaultMapper.toDto(entity.getColumn().getDataType(), entity.getDefaultSelectionOptions());
                    case SELECTION_CHECK ->
                            new JsonPrimitive(Optional.ofNullable(entity.getColumn().getDefaultValue().getBooleanValue()).map(Object::toString).orElse(Boolean.FALSE.toString()));
                    default -> JsonNull.INSTANCE;
                },

                entity.getColumn().getDefaultValue().getStringValue(),

                userGroupMapper.toDtoList(filterUnknownTypes(entity.getDefaultUserGroups())),
                entity.getColumn().getUserGroupAttributes().usergroupMultipleItems(),
                entity.getColumn().getUserGroupAttributes().usergroupSelectUsers(),
                entity.getColumn().getUserGroupAttributes().usergroupSelectGroups(),
                entity.getColumn().getUserGroupAttributes().usergroupSelectTeams(),
                entity.getColumn().getUserGroupAttributes().showUserStatus()
        );
    }

    @NonNull
    private Collection<UserGroup> filterUnknownTypes(@Nullable Collection<UserGroup> userGroups) {
        return Optional.ofNullable(userGroups)
                .orElse(Collections.emptyList())
                .stream()
                .filter(userGroup -> userGroup.getType() != EUserGroupType.UNKNOWN)
                .toList();
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
                Boolean.TRUE.equals(dto.usergroupSelectTeams()),
                Boolean.TRUE.equals(dto.showUserStatus())
        ));

        final var defaultValue = new Value();
        switch (column.getDataType()) {

            case NUMBER, NUMBER_PROGRESS, NUMBER_STARS -> Optional.ofNullable(dto.numberDefault())
                    .ifPresent(defaultValue::setDoubleValue);

            case SELECTION_MULTI -> Optional.ofNullable(dto.selectionDefault())
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonPrimitive.class::cast)
                    .filter(JsonPrimitive::isString)
                    .map(JsonPrimitive::getAsString)
                    .map(JsonParser::parseString)
                    .map(selectionDefaultMapper::selectionMultiToEntity)
                    .map(Collection::stream)
                    .map(stream -> stream
                            .map(remoteId -> new SelectionOption(remoteId, null))
                            .toList())
                    .ifPresent(entity::setDefaultSelectionOptions);

            case SELECTION -> Optional.ofNullable(dto.selectionDefault())
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonPrimitive.class::cast)
                    .filter(JsonPrimitive::isString)
                    .map(JsonPrimitive::getAsString)
                    .map(JsonParser::parseString)
                    .map(selectionDefaultMapper::selectionSingleToEntity)
                    .map(remoteId -> new SelectionOption(remoteId, null))
                    .map(Collections::singletonList)
                    .ifPresent(entity::setDefaultSelectionOptions);

            case SELECTION_CHECK -> Optional.ofNullable(dto.selectionDefault())
                    .map(selectionDefaultMapper::selectionCheckToEntity)
                    .ifPresent(defaultValue::setBooleanValue);

            case USERGROUP -> Optional.ofNullable(dto.usergroupDefault())
                    .map(userGroupMapper::toEntityList)
                    .ifPresent(entity::setDefaultUserGroups);

            default -> Optional.ofNullable(dto.textDefault())
                    .ifPresent(defaultValue::setStringValue);
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
