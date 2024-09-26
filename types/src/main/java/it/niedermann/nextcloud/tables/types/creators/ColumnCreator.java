package it.niedermann.nextcloud.tables.types.creators;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import org.jetbrains.annotations.NonBlocking;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import retrofit2.Call;

public interface ColumnCreator {

    @NonBlocking
    Call<OcsResponse<Column>> createColumn(@NonNull TablesAPI tablesAPI,
                                           long tableRemoteId,
                                           @NonNull Column column);
}
