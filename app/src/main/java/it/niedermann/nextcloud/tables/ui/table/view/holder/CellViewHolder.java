package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellLayoutBinding;

public class CellViewHolder extends AbstractViewHolder {
    private final TableviewCellLayoutBinding binding;

    public CellViewHolder(@NonNull TableviewCellLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Data cellModel, int columnPosition) {
        binding.data.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.data.setText(String.valueOf(cellModel.getValue()));
        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();
    }

}
