package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnRequestV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.EUserGroupTypeV1Dto;

public class ColumnRequestV1Mapper implements Function<FullColumn, ColumnRequestV1Dto> {

    @Override
    public ColumnRequestV1Dto apply(@NonNull FullColumn fullColumn) {
        final var column = fullColumn.getColumn();
        final var dataType = column.getDataType();
        final var textAttributes = column.getTextAttributes();
        final var numberAttributes = column.getNumberAttributes();
        final var userGroupAttributes = column.getUserGroupAttributes();

        return new ColumnRequestV1Dto(
                column.getTitle(),
                column.isMandatory(),
                column.getDescription(),

                column.getDefaultValue().getStringValue(),
                textAttributes.textAllowedPattern(),
                textAttributes.textMaxLength(),

                switch (dataType) {
                    case NUMBER, NUMBER_PROGRESS -> column.getDefaultValue().getDoubleValue();
                    case NUMBER_STARS -> Optional
                            .of(column)
                            .map(Column::getDefaultValue)
                            .map(Value::getDoubleValue)
                            .map(Math::round)
                            .map(Long::doubleValue)
                            .orElse(null);
                    default -> null;
                },
                numberAttributes.numberPrefix(),
                numberAttributes.numberSuffix(),
                numberAttributes.numberMin(),
                numberAttributes.numberMax(),
                numberAttributes.numberDecimals(),

                switch (dataType) {
                    case DATETIME, DATETIME_DATETIME -> Optional
                            .ofNullable(column.getDefaultValue().getInstantValue())
                            .map(TablesV1API.FORMATTER_DATA_DATE_TIME::format)
                            .orElse(null);
                    case DATETIME_DATE -> Optional
                            .ofNullable(column.getDefaultValue().getDateValue())
                            .map(TablesV1API.FORMATTER_DATA_DATE::format)
                            .orElse(null);
                    case DATETIME_TIME -> Optional
                            .ofNullable(column.getDefaultValue().getTimeValue())
                            .map(TablesV1API.FORMATTER_DATA_TIME::format)
                            .orElse(null);
                    default -> null;
                },

                switch (dataType) {
                    case SELECTION -> Optional.of(fullColumn)
                            .map(FullColumn::getDefaultSelectionOptions)
                            .map(selectionOptions -> {
                                final var jsonArray = new JsonArray();
                                selectionOptions
                                        .stream()
                                        .map(SelectionOption::getRemoteId)
                                        .forEach(jsonArray::add);
                                return jsonArray;
                            })
                            .map(JsonElement::toString)
                            .orElse(new JsonArray().toString());
                    case SELECTION_MULTI ->
                            Optional.of(fullColumn)
                                    .map(FullColumn::getDefaultSelectionOptions)
                                    .map(List::stream)
                                    .flatMap(Stream::findAny)
                                    .map(SelectionOption::getRemoteId)
                                    .map(JsonPrimitive::new)
                                    .map(JsonElement::toString)
                                    .orElse(null);
                    case SELECTION_CHECK ->
                            Optional.ofNullable(column.getDefaultValue().getBooleanValue())
                                    .map(JsonPrimitive::new)
                                    .map(JsonElement::toString)
                                    .orElse(null);
                    default -> null;
                },
                Optional.of(fullColumn.getSelectionOptions())
                        .map(selectionOptions -> {
                            final var jsonArray = new JsonArray();
                            for (final var selectionOption : selectionOptions) {
                                final var option = new JsonObject();
                                option.addProperty("id", selectionOption.getRemoteId());
                                option.addProperty("label", selectionOption.getLabel());
                            }
                            return jsonArray.toString();
                        })
                        .orElse(new JsonArray().toString()),

                fullColumn
                        .getDefaultUserGroups()
                        .stream()
                        .map(userGroup -> new ColumnRequestV1Dto.UserGroupV1Dto(
                                userGroup.getRemoteId(),
                                userGroup.getKey(),
                                EUserGroupTypeV1Dto.findByRemoteId(userGroup.getType().getRemoteId())
                        ))
                        .collect(Collectors.toUnmodifiableList()),
                userGroupAttributes.usergroupMultipleItems(),
                userGroupAttributes.usergroupSelectUsers(),
                userGroupAttributes.usergroupSelectGroups(),
                userGroupAttributes.showUserStatus()
        );
    }
}
