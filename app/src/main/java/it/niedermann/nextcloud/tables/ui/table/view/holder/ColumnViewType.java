package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Function;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime.DateCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime.DateTimeCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime.TimeCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.number.NumberCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.number.ProgressCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.number.StarsCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.selection.CheckCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.text.LineCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.text.LongCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.text.TextCellViewHolder;

public enum ColumnViewType {
    UNKNOWN(0, "", "", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    TEXT(1_000, "text", "", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    TEXT_LONG(1_001, "text", "long", layoutInflater -> new LongCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    TEXT_LINE(1_002, "text", "line", layoutInflater -> new LineCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    DATETIME(2_000, "datetime", "", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    DATETIME_DATETIME(2_001, "datetime", "datetime", layoutInflater -> new DateTimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    DATETIME_DATE(2_002, "datetime", "date", layoutInflater -> new DateCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    DATETIME_TIME(2_003, "datetime", "time", layoutInflater -> new TimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    SELECTION(3_000, "selection", "", layoutInflater -> new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    SELECTION_CHECK(3_001, "selection", "check", layoutInflater -> new CheckCellViewHolder(TableviewCellCheckBinding.inflate(layoutInflater))),
    NUMBER(4_000, "number", "", layoutInflater -> new NumberCellViewHolder(TableviewCellBinding.inflate(layoutInflater))),
    NUMBER_PROGRESS(4_001, "number", "progress", layoutInflater -> new ProgressCellViewHolder(TableviewCellProgressBinding.inflate(layoutInflater))),
    NUMBER_STARS(4_002, "number", "stars", layoutInflater -> new StarsCellViewHolder(TableviewCellStarsBinding.inflate(layoutInflater))),
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