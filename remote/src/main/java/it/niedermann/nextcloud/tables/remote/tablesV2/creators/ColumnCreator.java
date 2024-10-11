package it.niedermann.nextcloud.tables.remote.tablesV2.creators;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateColumnResponseV2Dto;
import retrofit2.Call;

public interface ColumnCreator {

    Call<OcsResponse<CreateColumnResponseV2Dto>> createColumn(@NonNull TablesV2API tablesV2API,
                                                              long tableRemoteId,
                                                              @NonNull ColumnV2Dto column);
}
