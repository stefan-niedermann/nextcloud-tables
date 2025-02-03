package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.DataV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.text.TextLinkRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.text.TextRemoteMapper;

public class TypeRemoteV2MapperServiceRegistry extends DataTypeServiceRegistry<DataV1Mapper> {

    public TypeRemoteV2MapperServiceRegistry() {
        super();
    }

    @Override
    public DataV1Mapper getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT_LINE, TEXT_RICH, TEXT_LONG ->
                    cache.computeIfAbsent(dataType, t -> new TextRemoteMapper());
            case TEXT_LINK -> cache.computeIfAbsent(dataType, t -> new TextLinkRemoteMapper());
            case DATETIME ->
                    new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.datetime.DateTimeRemoteMapper();
            case DATETIME_DATE ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.datetime.DateRemoteMapper());
            case DATETIME_TIME ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.datetime.TimeRemoteMapper());
            case NUMBER, NUMBER_PROGRESS ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.number.NumberRemoteMapper());
            case NUMBER_STARS ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.number.NumberStarsRemoteMapper());
            case SELECTION ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection.SelectionSingleDataV1Mapper());
            case SELECTION_MULTI ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection.SelectionMultiDataV1Mapper());
            case SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection.SelectionCheckDataV1Mapper());
            case UNKNOWN, USERGROUP ->
                    cache.computeIfAbsent(dataType, t -> new it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.unknown.UnknownRemoteMapper());
        };
    }
}
