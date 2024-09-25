package it.niedermann.nextcloud.tables.types.viewer;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.EDataType;
import it.niedermann.nextcloud.tables.types.databinding.TableviewColumnHeaderBinding;

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
        final var dataType = EDataType.findByColumn(column);

        return switch (dataType) {
            case NUMBER -> Gravity.CENTER_VERTICAL | Gravity.END;
            case SELECTION_CHECK -> Gravity.CENTER;
            default -> Gravity.CENTER_VERTICAL | Gravity.START;
        };
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
