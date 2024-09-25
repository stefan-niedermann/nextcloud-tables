package it.niedermann.nextcloud.tables.ui.table.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.TablesApplication.FeatureToggle;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.databinding.TableviewColumnHeaderBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCornerBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewRowHeaderBinding;
import it.niedermann.nextcloud.tables.types.EDataType;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;
import it.niedermann.nextcloud.tables.types.viewer.viewholder.selection.SelectionViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.ColumnHeaderViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.RowHeaderViewHolder;

public class TableViewAdapter extends AbstractTableAdapter<Column, Row, Data> {

    private final List<SelectionOption> selectionOptions = new ArrayList<>();

    @Override
    public int getCellItemViewType(int columnPosition) {
        final var column = getColumnHeaderItem(columnPosition);
        if (column == null) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException("Column header item on position " + columnPosition + " is null. Can not determine " + EDataType.class.getSimpleName());
            } else {
                return EDataType.UNKNOWN.getId();
            }
        }
        return EDataType.findByColumn(column).getId();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        return EDataType.findById(viewType).createViewHolder(parent);
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Data cellItemModel, int columnPosition, int rowPosition) {
        final var column = getColumnHeaderItem(columnPosition);

        try {
            if (column == null) {
                throw new NullPointerException("column header item at position " + columnPosition + " is null.");
            }

            if (holder instanceof SelectionViewHolder) {
                final JsonElement value;
                if(cellItemModel == null) {
                    value = JsonNull.INSTANCE;
                } else {
                    final var val = cellItemModel.getValue();
                    value = val == null ? JsonNull.INSTANCE : val;
                }

                ((SelectionViewHolder) holder).bind(value, column, selectionOptions);
            } else if (holder instanceof CellViewHolder) {
                ((CellViewHolder) holder).bind(cellItemModel, column);
            } else {
                throw new IllegalArgumentException("Unknown view holder type " + holder);
            }
        } catch (Exception e) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColumnHeaderViewHolder(TableviewColumnHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable Column columnHeaderItemModel, int columnPosition) {
        try {
            if (columnHeaderItemModel == null) {
                throw new NullPointerException("columnHeaderItemModel is null.");
            }

            if (holder instanceof ColumnHeaderViewHolder) {
                ((ColumnHeaderViewHolder) holder).bind(columnHeaderItemModel);
            } else {
                throw new IllegalArgumentException("Unknown view holder type " + holder);
            }
        } catch (Exception e) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowHeaderViewHolder(TableviewRowHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable Row rowHeaderItemModel, int rowPosition) {
        try {
            if (rowHeaderItemModel == null) {
                throw new NullPointerException("columnHeaderItemModel is null.");
            }

            if (holder instanceof RowHeaderViewHolder) {
                ((RowHeaderViewHolder) holder).bind(rowHeaderItemModel);
            } else {
                throw new IllegalArgumentException("Unknown view holder type " + holder);
            }
        } catch (Exception e) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }
        }
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return TableviewCornerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
    }

    @Override
    public void setAllItems(@Nullable List<Column> columnHeaderItems,
                            @Nullable List<Row> rowHeaderItems,
                            @Nullable List<List<Data>> cellItems) {
        setAllItems(columnHeaderItems, rowHeaderItems, cellItems, Collections.emptyList());
    }

    public void setAllItems(@Nullable List<Column> columnHeaderItems,
                            @Nullable List<Row> rowHeaderItems,
                            @Nullable List<List<Data>> cellItems,
                            @NonNull List<SelectionOption> selectionOptions) {
        this.selectionOptions.clear();
        this.selectionOptions.addAll(selectionOptions);
        super.setAllItems(columnHeaderItems, rowHeaderItems, cellItems);
    }
}
