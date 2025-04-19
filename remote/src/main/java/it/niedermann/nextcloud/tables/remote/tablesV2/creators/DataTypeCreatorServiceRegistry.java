package it.niedermann.nextcloud.tables.remote.tablesV2.creators;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.type.DateTimeCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.type.NumberCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.type.SelectionCheckCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.type.SelectionCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.type.TextCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.type.UserGroupCreator;

public class DataTypeCreatorServiceRegistry extends DataTypeServiceRegistry<ColumnCreator> {

    public DataTypeCreatorServiceRegistry() {
        super();
    }

    @Override
    public ColumnCreator getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT_LONG, TEXT_RICH, TEXT_LINE, TEXT_LINK ->
                    cache.computeIfAbsent(dataType, t -> new TextCreator());
            case DATETIME, DATETIME_DATE, DATETIME_TIME ->
                    cache.computeIfAbsent(dataType, t -> new DateTimeCreator());
            case SELECTION, SELECTION_MULTI ->
                    cache.computeIfAbsent(dataType, t -> new SelectionCreator());
            case SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new SelectionCheckCreator());
            case NUMBER, NUMBER_PROGRESS, NUMBER_STARS ->
                    cache.computeIfAbsent(dataType, t -> new NumberCreator());
            case USERGROUP -> cache.computeIfAbsent(dataType, t -> new UserGroupCreator());
            case UNKNOWN -> cache.computeIfAbsent(dataType, t -> new NoOpCreator());
        };
    }
}
