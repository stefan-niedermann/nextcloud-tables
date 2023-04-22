package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Function;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellSelectionBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.DateTimeCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.NumberCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.SelectionCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.TextCellViewHolder;

public enum ColumnViewType {
    UNKNOWN(-1, "", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    TEXT(0, "text", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    DATETIME(1, "datetime", layoutInflater -> new DateTimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    SELECTION(2, "selection", layoutInflater -> new SelectionCellViewHolder(TableviewCellSelectionBinding.inflate(layoutInflater))),
    NUMBER(3, "number", layoutInflater -> new NumberCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    ;
    private final int id;
    private final String type;
    private final Function<LayoutInflater, CellViewHolder> inflater;

    ColumnViewType(int id, @NonNull String type, @NonNull Function<LayoutInflater, CellViewHolder> inflater) {
        this.type = type;
        this.id = id;
        this.inflater = inflater;
    }

    public static ColumnViewType findByType(@NonNull String type) {
        for (final var entry : ColumnViewType.values()) {
            if (entry.type.equals(type)) {
                return entry;
            }
        }

        return ColumnViewType.UNKNOWN;
    }

    public static ColumnViewType findById(int id) {
        for (final var entry : ColumnViewType.values()) {
            if (entry.id == id) {
                return entry;
            }
        }

        return ColumnViewType.UNKNOWN;
    }

    public static ColumnViewType findByColumn(@Nullable Column column) {
        if (column == null) {
            return ColumnViewType.UNKNOWN;
        }

        return ColumnViewType.findByType(column.getType());
    }

    public CellViewHolder inflate(@NonNull Context context) {
        return inflater.apply(LayoutInflater.from(context));
    }

    public int getId() {
        return this.id;
    }
}