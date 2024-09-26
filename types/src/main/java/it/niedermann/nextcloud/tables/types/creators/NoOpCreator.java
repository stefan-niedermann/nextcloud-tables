package it.niedermann.nextcloud.tables.types.creators;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import retrofit2.Call;

public class NoOpCreator implements ColumnCreator {
    @Override
    public Call<OcsResponse<Column>> createColumn(@NonNull TablesAPI tablesAPI,
                                                  long tableRemoteId,
                                                  @NonNull Column column) {
        throw new UnsupportedOperationException("Can not create columns for the given type.");
    }
}
