package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.SelectionOptionV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateColumnV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UserGroupV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class UpdateColumnV1Mapper implements Mapper<UpdateColumnV1Dto, Column> {

    private final Mapper<SelectionOptionV1Dto, SelectionOption> selectionOptionMapper;
    private final Mapper<UserGroupV1Dto, UserGroup> userGroupMapper;

    public UpdateColumnV1Mapper() {
        this(
                new SelectionOptionV1Mapper(),
                new UserGroupV1Mapper()
        );
    }

    private UpdateColumnV1Mapper(
            @NonNull Mapper<SelectionOptionV1Dto, SelectionOption> selectionOptionMapper,
            @NonNull Mapper<UserGroupV1Dto, UserGroup> userGroupMapper
    ) {
        this.selectionOptionMapper = selectionOptionMapper;
        this.userGroupMapper = userGroupMapper;
    }

    @NonNull
    @Override
    public UpdateColumnV1Dto toDto(@NonNull Column entity) {
        return new UpdateColumnV1Dto(
                Objects.requireNonNullElse(entity.getTitle(), ""),
                entity.isMandatory(),
                entity.getDescription(),
                entity.getNumberPrefix(),
                entity.getNumberSuffix(),
                entity.getNumberDefault(),
                entity.getNumberMin(),
                entity.getNumberMax(),
                entity.getNumberDecimals(),
                entity.getTextDefault(),
                entity.getTextAllowedPattern(),
                entity.getTextMaxLength(),
                selectionOptionMapper.toDtoList(entity.getSelectionOptions()),
                serializeSelectionDefault(entity.getSelectionDefault()),
                entity.getDatetimeDefault(),
                userGroupMapper.toDtoList(filterUnknownTypes(entity.getUsergroupDefault())),
                entity.isUsergroupMultipleItems(),
                entity.isUsergroupSelectUsers(),
                entity.isUsergroupSelectGroups(),
                entity.isShowUserStatus());
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
    private String serializeSelectionDefault(@Nullable SelectionDefault selectionDefault) {
        return serializeSelectionDefault(
                selectionDefault == null ? null : selectionDefault.getValue());
    }

    @NonNull
    private String serializeSelectionDefault(@Nullable JsonElement value) {
        if (value == null) {
            return "";
        }

        return new JsonPrimitive(value.toString()).toString();
    }

    @NonNull
    @Override
    public Column toEntity(@NonNull UpdateColumnV1Dto dto) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
