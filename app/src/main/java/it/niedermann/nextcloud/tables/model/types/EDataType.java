package it.niedermann.nextcloud.tables.model.types;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.row.type.datetime.DateEditor;
import it.niedermann.nextcloud.tables.ui.row.type.datetime.DateTimeEditor;
import it.niedermann.nextcloud.tables.ui.row.type.datetime.TimeEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.NumberEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.ProgressEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.StarsEditor;
import it.niedermann.nextcloud.tables.ui.row.type.selection.CheckEditor;
import it.niedermann.nextcloud.tables.ui.row.type.selection.MultiEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextLineEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextLinkEditor;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;
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

public enum EDataType {

    UNKNOWN(0, "", ""),

    TEXT(1_000, "text", ""),
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(since = "0.5.0")
    TEXT_LONG(1_001, "text", "long"),
    TEXT_LINE(1_002, "text", "line"),
    TEXT_LINK(1_003, "text", "link"),
    TEXT_RICH(1_004, "text", "link"),

    DATETIME(2_000, "datetime", ""),
    DATETIME_DATETIME(2_001, "datetime", "datetime"),
    DATETIME_DATE(2_002, "datetime", "date"),
    DATETIME_TIME(2_003, "datetime", "time"),

    SELECTION(3_000, "selection", ""),
    SELECTION_MULTI(3_001, "selection", "multi"),
    SELECTION_CHECK(3_002, "selection", "check"),

    NUMBER(4_000, "number", ""),
    NUMBER_PROGRESS(4_001, "number", "progress"),
    NUMBER_STARS(4_002, "number", "stars"),
    ;

    private final int id;
    private final String type;
    private final String subType;

    public static EDataType findById(int id) {
        for (final var entry : EDataType.values()) {
            if (entry.id == id) {
                return entry;
            }
        }

        throw new NoSuchElementException("Unknown " + EDataType.class.getSimpleName() + " ID: " + id);
    }

    public static EDataType findByColumn(@Nullable Column column) {
        if (column == null) {
            return EDataType.UNKNOWN;
        }

        return EDataType.findByType(column.getType(), column.getSubtype());
    }

    public static EDataType findByType(@NonNull String type,
                                       @NonNull String subType) {
        for (final var entry : EDataType.values()) {
            if (entry.type.equals(type) && entry.subType.equals(subType)) {
                return entry;
            }
        }

        return EDataType.UNKNOWN;
    }

    EDataType(int id, @NonNull String type, @NonNull String subType) {
        this.type = type;
        this.subType = subType;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @NonNull
    public CellViewHolder createViewHolder(@NonNull ViewGroup parent) {
        final var layoutInflater = LayoutInflater.from(parent.getContext());
        switch (this) {
            case TEXT_RICH:
            case TEXT_LONG:
                return new LongCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
            case TEXT_LINE:
                return new LineCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
            case DATETIME:
            case DATETIME_DATETIME:
                return new DateTimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
            case DATETIME_DATE:
                return new DateCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
            case DATETIME_TIME:
                return new TimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
            case SELECTION_CHECK:
                return new CheckCellViewHolder(TableviewCellCheckBinding.inflate(layoutInflater, parent, false));
            case NUMBER:
                return new NumberCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
            case NUMBER_PROGRESS:
                return new ProgressCellViewHolder(TableviewCellProgressBinding.inflate(layoutInflater, parent, false));
            case NUMBER_STARS:
                return new StarsCellViewHolder(TableviewCellStarsBinding.inflate(layoutInflater, parent, false));
            case TEXT:
            case TEXT_LINK:
            case SELECTION:
            case UNKNOWN:
            default:
                return new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
        }
    }

    @NonNull
    public ColumnEditView createEditor(@NonNull Context context,
                                       @NonNull Column column,
                                       @Nullable Data data,
                                       @Nullable FragmentManager fragmentManager) {
        final Data dataToPass = ensureDataObjectPresent(column, data);

        switch (this) {
            case TEXT_LINE:
                return new TextLineEditor(context, fragmentManager, column, dataToPass);
            case TEXT_LINK:
                return new TextLinkEditor(context, fragmentManager, column, dataToPass);
            case DATETIME_DATETIME:
            case DATETIME:
                return new DateTimeEditor(context, fragmentManager, column, dataToPass);
            case DATETIME_DATE:
                return new DateEditor(context, fragmentManager, column, dataToPass);
            case DATETIME_TIME:
                return new TimeEditor(context, fragmentManager, column, dataToPass);
            case NUMBER:
                return new NumberEditor(context, fragmentManager, column, dataToPass);
            case NUMBER_STARS:
                return new StarsEditor(context, fragmentManager, column, dataToPass);
            case NUMBER_PROGRESS:
                return new ProgressEditor(context, fragmentManager, column, dataToPass);
            case SELECTION_MULTI:
                return new MultiEditor(context, fragmentManager, column, dataToPass);
            case SELECTION_CHECK:
                return new CheckEditor(context, fragmentManager, column, dataToPass);
            case UNKNOWN:
            case TEXT:
            case TEXT_RICH:
            default:
                return new TextEditor(context, fragmentManager, column, dataToPass);
        }
    }

    /**
     * Ensures the given data property is not null. In case its value is null, the value will be
     * initialized with the default value according the given column.
     */
    @NonNull
    private Data ensureDataObjectPresent(@NonNull Column column, @Nullable Data data) {
        final Data dataToPass;

        if (data != null) {
            dataToPass = data;

            final var value = data.getValue();
            if (value == null) {
                dataToPass.setValue(column.getDefaultValueByType());
            }

        } else {
            dataToPass = new Data();
            dataToPass.setAccountId(column.getAccountId());
            dataToPass.setColumnId(column.getId());
            dataToPass.setRemoteColumnId(column.getRemoteId());
            dataToPass.setValue(column.getDefaultValueByType());
        }

        return dataToPass;
    }
}
