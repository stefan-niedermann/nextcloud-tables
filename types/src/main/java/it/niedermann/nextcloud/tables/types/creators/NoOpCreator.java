package it.niedermann.nextcloud.tables.types.creators;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import retrofit2.Call;

public class NoOpCreator implements ColumnCreator {

    @Override
    public Call<OcsResponse<ColumnV2Dto>> createColumn(@NonNull TablesV2API tablesV2API,
                                                       long tableRemoteId,
                                                       @NonNull ColumnV2Dto column) {
        throw new UnsupportedOperationException("Can not create columns for the given type.");
    }
}
