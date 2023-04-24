package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class StarsCellViewHolder extends CellViewHolder {

    private final TableviewCellStarsBinding binding;

    public StarsCellViewHolder(@NonNull TableviewCellStarsBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            final var def = column.getNumberDefault();
            if (def == null) {
                setStars(0);
            } else {
                setStars(def.intValue());
            }
        } else {
            try {
                final var stars = Double.parseDouble(String.valueOf(data.getValue()));
                setStars((int) stars);
            } catch (NumberFormatException noDoubleException) {
                setStars(0);
            }
        }
        binding.cellContainer.requestLayout();
    }

    private void setStars(int count) {
        for (int i = 0; i < 5; i++) {
            final var child = binding.cellContainer.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setImageResource(i < count ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
            } else {
                throw new IllegalStateException("Expected child at position " + i + " to be of type " + ImageView.class.getSimpleName() + " but was " + child.getClass().getSimpleName());
            }
        }
    }
}
