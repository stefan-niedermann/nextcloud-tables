package it.niedermann.nextcloud.tables.types.creators.type;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.remote.model.columns.NumberColumn;
import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import retrofit2.Call;

public class NumberCreator implements ColumnCreator {
    @Override
    public Call<OcsResponse<Column>> createColumn(@NonNull TablesAPI tablesAPI,
                                                  long tableRemoteId,
                                                  @NonNull Column column) {
        return createColumn(tablesAPI, new NumberColumn(tableRemoteId, column));
    }

    private Call<OcsResponse<Column>> createColumn(@NonNull TablesAPI tablesAPI,
                                                   @NonNull NumberColumn column) {
        return tablesAPI.createNumberColumn(column);
    }
}
