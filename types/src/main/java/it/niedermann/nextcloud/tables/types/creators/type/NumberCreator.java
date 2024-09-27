package it.niedermann.nextcloud.tables.types.creators.type;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.columns.CreateNumberColumnV2Dto;
import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import retrofit2.Call;

public class NumberCreator implements ColumnCreator {
    @Override
    public Call<OcsResponse<ColumnV2Dto>> createColumn(@NonNull TablesV2API tablesV2API,
                                                       long tableRemoteId,
                                                       @NonNull ColumnV2Dto column) {
        return createColumn(tablesV2API, new CreateNumberColumnV2Dto(tableRemoteId, column));
    }

    private Call<OcsResponse<ColumnV2Dto>> createColumn(@NonNull TablesV2API tablesV2API,
                                                   @NonNull CreateNumberColumnV2Dto column) {
        return tablesV2API.createNumberColumn(column);
    }
}
