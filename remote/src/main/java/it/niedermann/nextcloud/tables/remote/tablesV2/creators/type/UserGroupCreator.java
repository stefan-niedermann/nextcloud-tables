package it.niedermann.nextcloud.tables.remote.tablesV2.creators.type;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateColumnResponseV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.columns.CreateUserGroupColumnV2Dto;
import retrofit2.Call;

public class UserGroupCreator implements ColumnCreator {
    @Override
    public Call<OcsResponse<CreateColumnResponseV2Dto>> createColumn(@NonNull TablesV2API tablesV2API,
                                                                     long tableRemoteId,
                                                                     @NonNull ColumnV2Dto column) {
        return createColumn(tablesV2API, new CreateUserGroupColumnV2Dto(tableRemoteId, column));
    }

    private Call<OcsResponse<CreateColumnResponseV2Dto>> createColumn(@NonNull TablesV2API tablesV2API,
                                                                      @NonNull CreateUserGroupColumnV2Dto column) {
        return tablesV2API.createUserGroupColumn(column);
    }
}
