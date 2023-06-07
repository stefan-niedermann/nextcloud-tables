package it.niedermann.nextcloud.tables.ui.table.view.holder.type.selection;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class SelectionMultiViewHolder extends SelectionViewHolder {

    public SelectionMultiViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column, @NonNull List<SelectionOption> selectionOptions) {
        binding.data.setText(data == null ? null : formatValue(data.getValue(), column.getId(), selectionOptions));

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.getRoot().requestLayout();
    }
}
