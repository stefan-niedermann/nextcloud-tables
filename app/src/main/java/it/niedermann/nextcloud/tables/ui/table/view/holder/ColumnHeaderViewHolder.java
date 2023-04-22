package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.TableviewColumnHeaderBinding;

/**
 * Created by evrencoskun on 1.12.2017.
 */

public class ColumnHeaderViewHolder extends AbstractSorterViewHolder {

    private final TableviewColumnHeaderBinding binding;

    public ColumnHeaderViewHolder(@NonNull TableviewColumnHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Column columnHeaderModel, int columnPosition) {
        binding.columnHeaderTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.columnHeaderTextView.setText(columnHeaderModel.getTitle());
//        binding.columnHeaderContainer.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.columnHeaderTextView.requestLayout();
    }

    @Override
    public void onSortingStatusChanged(@NonNull SortState sortState) {
        super.onSortingStatusChanged(sortState);
        binding.columnHeaderContainer.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.columnHeaderTextView.requestLayout();
        binding.columnHeaderContainer.requestLayout();
        itemView.requestLayout();
    }
}
