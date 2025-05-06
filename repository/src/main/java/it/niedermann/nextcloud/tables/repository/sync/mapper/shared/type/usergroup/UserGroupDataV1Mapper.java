package it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.usergroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.DataV1Mapper;

public class UserGroupDataV1Mapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        final var userGroups = Optional
                .of(entity)
                .map(FullData::getUserGroups)
                .map(Collection::stream)
                .map(userGroupStream -> userGroupStream.map((userGroup) -> {
                    final var jsonObject = new JsonObject();
                    jsonObject.addProperty("id", userGroup.getRemoteId());
                    jsonObject.addProperty("type", userGroup.getType().getRemoteType());
                    // TODO
                    // jsonObject.addProperty("displayName", userGroup.getType().getRemoteId());
                    return jsonObject;
                }))
                .map(stream -> stream.map(JsonElement.class::cast))
                .map(stream -> stream.reduce(new JsonArray(), (arr, elem) -> {
                    arr.getAsJsonArray().add(elem);
                    return arr;
                }));

        return userGroups.orElse(JsonNull.INSTANCE);
    }

    @Override
    protected void toFullData(@NonNull FullData fullData,
                              @Nullable JsonElement value,
                              @NonNull FullColumn fullColumn,
                              @NonNull TablesVersion version) {
        Optional.ofNullable(value)
                .filter(JsonElement::isJsonArray)
                .map(JsonElement::getAsJsonArray)
                .map(JsonArray::asList)
                .map(userGroups -> mapToUserGroups(userGroups, fullColumn.getColumn().getAccountId()))
                .ifPresent(fullData::setUserGroups);
    }

    @NonNull
    private List<UserGroup> mapToUserGroups(@NonNull Collection<JsonElement> userGroups, long accountId) {
        return userGroups
                .stream()
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .map(json -> mapToUserGroup(json, accountId))
                .toList();
    }

    @NonNull
    private UserGroup mapToUserGroup(@NonNull JsonObject userGroup, long accountId) {
        final var entity = new UserGroup();
        entity.setAccountId(accountId);
        entity.setRemoteId(userGroup.get("id").getAsString());
        entity.setType(EUserGroupType.findByRemoteId(userGroup.get("type").getAsInt()));
        // TODO
        // entity.setDisplayName(userGroup.get("displayName").getAsString());
        return entity;
    }
}