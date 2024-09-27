package it.niedermann.nextcloud.tables.types.creators;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import org.jetbrains.annotations.NonBlocking;

import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import retrofit2.Call;

public interface ColumnCreator {

    @NonBlocking
    Call<OcsResponse<ColumnV2Dto>> createColumn(@NonNull TablesV2API tablesV2API,
                                                long tableRemoteId,
                                                @NonNull ColumnV2Dto column);
}
