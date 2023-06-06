package it.niedermann.nextcloud.tables.ui.table.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.databinding.TableviewColumnHeaderBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCornerBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewRowHeaderBinding;
import it.niedermann.nextcloud.tables.model.EDataType;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.ColumnHeaderViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.RowHeaderViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.selection.SelectionViewHolder;

public class TableViewAdapter extends AbstractTableAdapter<Column, Row, Data> {

    private final CellViewHolder.Factory cellViewHolderFactory;
    private final List<SelectionOption> selectionOptions = new ArrayList<>();

    public TableViewAdapter() {
        this(new CellViewHolder.Factory());
    }

    private TableViewAdapter(@NonNull CellViewHolder.Factory cellViewHolderFactory) {
        this.cellViewHolderFactory = cellViewHolderFactory;
    }

    @Override
    public int getCellItemViewType(int columnPosition) {
        return EDataType.findByColumn(getColumnHeaderItem(columnPosition)).getId();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        return cellViewHolderFactory.create(EDataType.findById(viewType), parent);
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Data cellItemModel, int columnPosition, int rowPosition) {
        if (holder instanceof SelectionViewHolder) {
            ((SelectionViewHolder) holder).bind(cellItemModel, getColumnHeaderItem(columnPosition), selectionOptions);
        } else if (holder instanceof CellViewHolder) {
            ((CellViewHolder) holder).bind(cellItemModel, getColumnHeaderItem(columnPosition));
        } else {
            throw new IllegalArgumentException("Unknown view holder type " + holder);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColumnHeaderViewHolder(TableviewColumnHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable Column columnHeaderItemModel, int columnPosition) {
        if (holder instanceof ColumnHeaderViewHolder) {
            ((ColumnHeaderViewHolder) holder).bind(columnHeaderItemModel);
        } else {
            throw new IllegalArgumentException("Unknown view holder type " + holder);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowHeaderViewHolder(TableviewRowHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @NonNull Row rowHeaderItemModel, int rowPosition) {
        if (holder instanceof RowHeaderViewHolder) {
            ((RowHeaderViewHolder) holder).bind(rowHeaderItemModel);
        } else {
            throw new IllegalArgumentException("Unknown view holder type " + holder);
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
