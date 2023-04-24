package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Function;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellSelectionBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.DateTimeCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.NumberCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.ProgressCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.SelectionCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.StarsCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.TextCellViewHolder;

public enum ColumnViewType {
    UNKNOWN(-1_000, "", "", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    TEXT(0, "text", "", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    DATETIME(1_000, "datetime", "", layoutInflater -> new DateTimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    SELECTION(2_000, "selection", "", layoutInflater -> new SelectionCellViewHolder(TableviewCellSelectionBinding.inflate(layoutInflater))),
    CHECK(2_001, "selection", "check", layoutInflater -> new SelectionCellViewHolder(TableviewCellSelectionBinding.inflate(layoutInflater))),
    NUMBER(3_000, "number", "", layoutInflater -> new NumberCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    PROGRESS(3_001, "number", "progress", layoutInflater -> new ProgressCellViewHolder(TableviewCellProgressBinding.inflate(layoutInflater))),
    STARS(3_002, "number", "stars", layoutInflater -> new StarsCellViewHolder(TableviewCellStarsBinding.inflate(layoutInflater))),
    ;
    private final int id;
    private final String type;
    private final String subType;
    private final Function<LayoutInflater, CellViewHolder> inflater;

    ColumnViewType(int id, @NonNull String type, @NonNull String subType, @NonNull Function<LayoutInflater, CellViewHolder> inflater) {
        this.type = type;
        this.subType = subType;
        this.id = id;
        this.inflater = inflater;
    }

    public static ColumnViewType findByType(@NonNull String type, @NonNull String subType) {
        for (final var entry : ColumnViewType.values()) {
            if (entry.type.equals(type) && entry.subType.equals(subType)) {
                return entry;
            }
        }

        for (final var entry : ColumnViewType.values()) {
            if (entry.type.equals(type) && TextUtils.isEmpty(subType)) {
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

        return ColumnViewType.findByType(column.getType(), column.getSubtype());
    }

    public CellViewHolder inflate(@NonNull Context context) {
        return inflater.apply(LayoutInflater.from(context));
    }

    public int getId() {
        return this.id;
    }
}