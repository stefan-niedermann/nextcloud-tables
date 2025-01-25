package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.text;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.LinkValue;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.LinkValueWithProviderId;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.DataV1Mapper;

public class TextLinkRemoteMapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        return Optional
                .of(entity)
                .map(FullData::getLinkValueWithProviderRemoteId)
                .map(linkValueWithProviderRemoteId -> {
                    final var json = new JsonObject();

                    final var link = Optional.of(linkValueWithProviderRemoteId);

                    final var linkValue = link
                            .map(LinkValueWithProviderId::getLinkValue);

                    final var searchProvider = link
                            .map(LinkValueWithProviderId::getProviderId)
                            .orElse(TablesV1API.TEXT_LINK_PROVIDER_ID_URL);

                    final var value = linkValue.map(LinkValue::getValue)
                            .map(Uri::toString)
                            .orElse("");

                    final var title = linkValue.map(LinkValue::getTitle)
                            .filter(String::isBlank)
                            .orElse(value);

                    json.addProperty("title", title);

                    linkValue.map(LinkValue::getSubline)
                            .ifPresent(subline -> json.addProperty("subline", subline));

                    json.addProperty("value", value);

                    json.addProperty("providerId", searchProvider);

                    return json;
                })
                .map(JsonElement.class::cast)
                .orElse(JsonNull.INSTANCE);
    }

    @Override
    protected void toFullData(@NonNull FullData fullData,
                              @Nullable JsonElement value,
                              @NonNull FullColumn fullColumn,
                              @NonNull TablesVersion version) {
        Optional.ofNullable(value)
                .filter(v -> v != JsonNull.INSTANCE)
                .map(JsonElement::getAsString)
                .map(JsonParser::parseString)
                .map(json -> {
                    if (!json.isJsonObject()) {
                        return null;
                    }

                    final var jsonObject = json.getAsJsonObject();

                    if (!jsonObject.has("value")) {
                        return null;
                    }

                    final var linkValue = new LinkValue();

                    linkValue.setDataId(fullData.getData().getId());

                    Optional.ofNullable(jsonObject.get("value"))
                            .filter(JsonElement::isJsonPrimitive)
                            .map(JsonElement::getAsString)
                            .map(Uri::parse)
                            .ifPresent(linkValue::setValue);

                    Optional.ofNullable(jsonObject.get("title"))
                            .filter(JsonElement::isJsonPrimitive)
                            .map(JsonElement::getAsString)
                            .ifPresent(linkValue::setTitle);

                    Optional.ofNullable(jsonObject.get("subline"))
                            .filter(JsonElement::isJsonPrimitive)
                            .map(JsonElement::getAsString)
                            .ifPresent(linkValue::setSubline);

                    final var providerId = Optional.ofNullable(jsonObject.get("providerId"))
                            .filter(JsonElement::isJsonPrimitive)
                            .map(JsonElement::getAsString)
                            .orElse(null);

                    return new LinkValueWithProviderId(linkValue, providerId);
                })
                .filter(Objects::nonNull)
                .ifPresent(fullData::setLinkValueWithProviderRemoteId);
    }

}
