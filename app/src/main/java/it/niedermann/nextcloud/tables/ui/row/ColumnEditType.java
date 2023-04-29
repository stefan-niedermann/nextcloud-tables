package it.niedermann.nextcloud.tables.ui.row;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.type.number.NumberEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.ProgressEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.StarsEditor;
import it.niedermann.nextcloud.tables.ui.row.type.selection.CheckEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextLineEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextLinkEditor;

public enum ColumnEditType {
    UNKNOWN(0, "", "", TextEditor::new),
    TEXT(1_000, "text", "", TextEditor::new),
    TEXT_LINE(1_001, "text", "line", TextLineEditor::new),
    TEXT_LINK(1_002, "text", "link", TextLinkEditor::new),
    NUMBER(4_000, "number", "", NumberEditor::new),
    NUMBER_STAR(4_001, "number", "stars", StarsEditor::new),
    NUMBER_PROGRESS(4_002, "number", "progress", ProgressEditor::new),
    SELECTION_CHECK(3_001, "selection", "check", CheckEditor::new),
    ;
    private final int id;
    private final String type;
    private final String subType;
    private final ColumnEditView.Factory factory;

    ColumnEditType(int id,
                   @NonNull String type,
                   @NonNull String subType,
                   @NonNull ColumnEditView.Factory factory) {
        this.type = type;
        this.subType = subType;
        this.id = id;
        this.factory = factory;
    }

    public static ColumnEditType findByType(@NonNull String type,
                                            @NonNull String subType) {
        for (final var entry : ColumnEditType.values()) {
            if (entry.type.equals(type) && entry.subType.equals(subType)) {
                return entry;
            }
        }

        for (final var entry : ColumnEditType.values()) {
            if (entry.type.equals(type) && TextUtils.isEmpty(subType)) {
                return entry;
            }
        }

        return ColumnEditType.UNKNOWN;
    }

    public ColumnEditView inflate(@NonNull Context context,
                                  @NonNull Column column) {
        return factory.create(context, column, null);
    }

    public ColumnEditView inflate(@NonNull Context context,
                                  @NonNull Column column,
                                  @Nullable Object value) {
        return factory.create(context, column, value == null ? column.getDefaultValueByType() : value);
    }

    public int getId() {
        return this.id;
    }
}