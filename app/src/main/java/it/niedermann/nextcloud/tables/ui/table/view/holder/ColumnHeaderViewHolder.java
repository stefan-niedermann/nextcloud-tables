package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.TableviewColumnHeaderBinding;
import it.niedermann.nextcloud.tables.model.types.EDataType;

/**
 * Created by evrencoskun on 1.12.2017.
 */

public class ColumnHeaderViewHolder extends AbstractSorterViewHolder {

    private final TableviewColumnHeaderBinding binding;

    public ColumnHeaderViewHolder(@NonNull TableviewColumnHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Column column) {
        binding.columnHeaderTextView.setGravity(getGravity(column));
        binding.columnHeaderTextView.setText(column.getTitle());
        binding.columnHeaderTextView.setContentDescription(column.getDescription());

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    private int getGravity(@NonNull Column column) {
        final var type = EDataType.findByColumn(column);

        switch (type) {
            case NUMBER:
                return Gravity.CENTER_VERTICAL | Gravity.END;
            case SELECTION_CHECK:
                return Gravity.CENTER;
            default:
                return Gravity.CENTER_VERTICAL | Gravity.START;
        }
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
