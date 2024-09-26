package it.niedermann.nextcloud.tables.types;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.google.gson.JsonElement;
import com.nextcloud.android.sso.model.ocs.OcsResponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.datetime.DateDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.datetime.DateTimeDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.datetime.TimeDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.number.NumberDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.number.ProgressDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.number.StarsDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.selection.SelectionCheckDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.selection.SelectionDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.selection.SelectionMultiDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.text.LineDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.text.LinkDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.text.RichViewDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.text.TextDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.unknown.UnknownDescriptor;
import it.niedermann.nextcloud.tables.types.descriptors.usergroup.UserGroupDescriptor;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;
import retrofit2.Call;

public enum EDataType {

    UNKNOWN(0, "", "", new UnknownDescriptor()),

    TEXT(1_000, "text", "", new TextDescriptor()),
    @Deprecated(since = "0.5.0")
    TEXT_LONG(1_001, "text", "long", new RichViewDescriptor()),
    TEXT_LINE(1_002, "text", "line", new LineDescriptor()),
    TEXT_LINK(1_003, "text", "link", new LinkDescriptor()),
    /** @since 0.5.0 */
    TEXT_RICH(1_004, "text", "rich", new RichViewDescriptor()),

    DATETIME(2_000, "datetime", "", new DateTimeDescriptor()),
    DATETIME_DATETIME(2_001, "datetime", "datetime", new DateTimeDescriptor()),
    DATETIME_DATE(2_002, "datetime", "date", new DateDescriptor()),
    DATETIME_TIME(2_003, "datetime", "time", new TimeDescriptor()),

    SELECTION(3_000, "selection", "", new SelectionDescriptor()),
    /** @since 0.5.0 */
    SELECTION_MULTI(3_001, "selection", "multi", new SelectionMultiDescriptor()),
    SELECTION_CHECK(3_002, "selection", "check", new SelectionCheckDescriptor()),

    NUMBER(4_000, "number", "", new NumberDescriptor()),
    NUMBER_PROGRESS(4_001, "number", "progress", new ProgressDescriptor()),
    NUMBER_STARS(4_002, "number", "stars", new StarsDescriptor()),

    /** @since 0.8.0 */
    USERGROUP(5_000, "usergroup", "", new UserGroupDescriptor()),
    ;

    private final int id;
    private final String type;
    private final String subType;
    private final DataTypeDescriptor descriptor;

    public static EDataType findById(int id) throws NoSuchElementException {
        for (final var entry : EDataType.values()) {
            if (entry.id == id) {
                return entry;
            }
        }

        throw new NoSuchElementException("Unknown " + EDataType.class.getSimpleName() + " ID: " + id);
    }

    public static EDataType findByColumn(@NonNull Column column) {
        return findByType(column.getType(), column.getSubtype());
    }

    public static EDataType findByType(@NonNull String type, @NonNull String subType) {
        for (final var entry : EDataType.values()) {
            if (entry.type.equals(type) && entry.subType.equals(subType)) {
                return entry;
            }
        }

        if (BuildConfig.DEBUG) {
            throw new UnsupportedOperationException("Unknown column type: " + type + "/" + subType);
        }

        return EDataType.UNKNOWN;
    }

    EDataType(int id,
              @NonNull String type,
              @NonNull String subType,
              @NonNull DataTypeDescriptor descriptor) {
        this.type = type;
        this.subType = subType;
        this.id = id;
        this.descriptor = descriptor;
    }

    public int getId() {
        return this.id;
    }

    @NonNull
    public static Collection<String> getTypes() {
        return Arrays
                .stream(values())
                .map(value -> value.type)
                .collect(Collectors.toUnmodifiableSet());
    }

    @NonNull
    public static Collection<String> getSubTypes(@NonNull String type) {
        return Arrays
                .stream(values())
                .filter(value -> value.type.equals(type))
                .map(value -> value.subType)
                .collect(Collectors.toUnmodifiableSet());
    }

    @NonNull
    public AbstractViewHolder createViewHolder(@NonNull ViewGroup parent) {
        return this.descriptor.getViewHolderFactory().create(parent);
    }

    @NonNull
    public ColumnEditView createEditor(@NonNull Context context,
                                       @NonNull Column column,
                                       @Nullable Data data,
                                       @Nullable FragmentManager fragmentManager) throws Exception {
        return this.descriptor.getEditorFactory().create(context, column, data, fragmentManager);
    }

    @NonNull
    public ColumnManageView createManager(@NonNull Context context,
                                          @NonNull Column column,
                                          @Nullable FragmentManager fragmentManager) {
        return this.descriptor.getManageFactory().create(context, column, fragmentManager);
    }

    @NonNull
    public JsonElement interceptRequest(@NonNull TablesVersion version, @NonNull JsonElement value) {
        return this.descriptor.getInterceptor().interceptRequest(version, value);
    }

    @NonNull
    public JsonElement interceptResponse(@NonNull TablesVersion version, @NonNull JsonElement value) {
        return this.descriptor.getInterceptor().interceptResponse(version, value);
    }

    @NonNull
    public Call<OcsResponse<Column>> createColumn(@NonNull TablesAPI tablesAPI,
                                                  long tableRemoteId,
                                                  @NonNull Column column) {
        return this.descriptor.getColumnCreator().createColumn(tablesAPI, tableRemoteId, column);
    }
}
