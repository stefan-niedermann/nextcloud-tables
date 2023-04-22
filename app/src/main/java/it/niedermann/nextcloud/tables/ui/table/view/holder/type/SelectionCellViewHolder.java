package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellSelectionBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class SelectionCellViewHolder extends CellViewHolder {

    private static final String SELECTION_CHECK_TRUE = "true";
    private static final String SELECTION_CHECK_FALSE = "false";

    private final TableviewCellSelectionBinding binding;

    public SelectionCellViewHolder(@NonNull TableviewCellSelectionBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull Data data, @NonNull Column column) {
        final var subtype = column.getSubtype();
        //noinspection SwitchStatementWithTooFewBranches
        switch (subtype) {
            case "check": {
                if (SELECTION_CHECK_TRUE.equals(data.getValue())) {
                    binding.check.setSelected(true);
                } else if (SELECTION_CHECK_FALSE.equals(data.getValue())) {
                    binding.check.setSelected(false);
                } else {
                    binding.check.setSelected(false);
                }
                break;
            }
            default: {
                // TODO
                break;
            }
        }
        binding.check.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.check.requestLayout();
    }
}
