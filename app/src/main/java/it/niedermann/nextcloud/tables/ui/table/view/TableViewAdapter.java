package it.niedermann.nextcloud.tables.ui.table.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.databinding.TableviewCellLayoutBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewColumnHeaderLayoutBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewRowHeaderLayoutBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.ColumnHeaderViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.RowHeaderViewHolder;

public class TableViewAdapter extends AbstractTableAdapter<Column, Row, Data> {

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CellViewHolder(TableviewCellLayoutBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Data cellItemModel, int columnPosition, int rowPosition) {
        if(holder instanceof CellViewHolder) {
            ((CellViewHolder) holder).bind(cellItemModel, getColumnHeaderItem(columnPosition));
        } else {
            throw new IllegalArgumentException("Unknown view holder type " + holder);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColumnHeaderViewHolder(TableviewColumnHeaderLayoutBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable Column columnHeaderItemModel, int columnPosition) {
        if(holder instanceof ColumnHeaderViewHolder) {
            ((ColumnHeaderViewHolder) holder).bind(columnHeaderItemModel, columnPosition);
        } else {
            throw new IllegalArgumentException("Unknown view holder type " + holder);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowHeaderViewHolder(TableviewRowHeaderLayoutBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @NonNull Row rowHeaderItemModel, int rowPosition) {
        if(holder instanceof RowHeaderViewHolder) {
            ((RowHeaderViewHolder) holder).bind(rowHeaderItemModel);
        } else {
            throw new IllegalArgumentException("Unknown view holder type " + holder);
        }
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return null;
    }
}
