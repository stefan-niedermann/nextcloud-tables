package it.niedermann.nextcloud.tables.ui.row;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.BiFunction;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.type.number.NumberEditor;
import it.niedermann.nextcloud.tables.ui.row.type.selection.CheckEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextLineEditor;

public enum ColumnEditType {
    UNKNOWN(0, "", "", TextEditor::new),
    TEXT(1_000, "text", "", TextEditor::new),
    TEXT_LINE(1_001, "text", "", TextLineEditor::new),
    NUMBER(4_000, "number", "", NumberEditor::new),
    SELECTION_CHECK(3_001, "selection", "check", CheckEditor::new),
    ;
    private final int id;
    private final String type;
    private final String subType;
    private final BiFunction<Context, Column, ColumnEditView> inflater;

    ColumnEditType(int id, @NonNull String type, @NonNull String subType, @NonNull BiFunction<Context, Column, ColumnEditView> inflater) {
        this.type = type;
        this.subType = subType;
        this.id = id;
        this.inflater = inflater;
    }

    public static ColumnEditType findByType(@NonNull String type, @NonNull String subType) {
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

    public static ColumnEditType findById(int id) {
        for (final var entry : ColumnEditType.values()) {
            if (entry.id == id) {
                return entry;
            }
        }

        return ColumnEditType.UNKNOWN;
    }

    public static ColumnEditType findByColumn(@Nullable Column column) {
        if (column == null) {
            return ColumnEditType.UNKNOWN;
        }

        return ColumnEditType.findByType(column.getType(), column.getSubtype());
    }

    public ColumnEditView inflate(@NonNull Context context, @NonNull Column column) {
        return inflater.apply(context, column);
    }

    public int getId() {
        return this.id;
    }
}