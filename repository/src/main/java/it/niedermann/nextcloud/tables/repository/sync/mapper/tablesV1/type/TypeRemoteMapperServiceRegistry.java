package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.datetime.DateRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.datetime.DateTimeRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.datetime.TimeRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.number.NumberRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.number.NumberStarsRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection.SelectionCheckDataV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection.SelectionMultiDataV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection.SelectionSingleDataV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.text.TextRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.unknown.UnknownRemoteMapper;

public class TypeRemoteMapperServiceRegistry extends DataTypeServiceRegistry<DataV1Mapper> {

    public TypeRemoteMapperServiceRegistry() {
        super();
    }

    @Override
    public DataV1Mapper getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT, TEXT_LINE, TEXT_LINK, TEXT_RICH, TEXT_LONG ->
                    cache.computeIfAbsent(dataType, t -> new TextRemoteMapper());
            case DATETIME, DATETIME_DATETIME -> new DateTimeRemoteMapper();
            case DATETIME_DATE -> cache.computeIfAbsent(dataType, t -> new DateRemoteMapper());
            case DATETIME_TIME -> cache.computeIfAbsent(dataType, t -> new TimeRemoteMapper());
            case NUMBER, NUMBER_PROGRESS ->
                    cache.computeIfAbsent(dataType, t -> new NumberRemoteMapper());
            case NUMBER_STARS -> cache.computeIfAbsent(dataType, t -> new NumberStarsRemoteMapper());
            case SELECTION ->
                    cache.computeIfAbsent(dataType, t -> new SelectionSingleDataV1Mapper());
            case SELECTION_MULTI ->
                    cache.computeIfAbsent(dataType, t -> new SelectionMultiDataV1Mapper());
            case SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new SelectionCheckDataV1Mapper());
            case UNKNOWN, USERGROUP ->
                    cache.computeIfAbsent(dataType, t -> new UnknownRemoteMapper());
        };
    }
}
