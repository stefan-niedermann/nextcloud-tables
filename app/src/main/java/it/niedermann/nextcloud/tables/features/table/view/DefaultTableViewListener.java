package it.niedermann.nextcloud.tables.features.table.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.listener.ITableViewListener;

/**
 * Provides default implementation for all methods in {@link ITableViewListener} for convenience.
 */
public interface DefaultTableViewListener extends ITableViewListener {

    default void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
    }

    default void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
    }

    default void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
    }

    default void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
    }

    default void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
    }

    default void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
    }

    default void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
    }

    default void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
    }

    default void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
    }


}
