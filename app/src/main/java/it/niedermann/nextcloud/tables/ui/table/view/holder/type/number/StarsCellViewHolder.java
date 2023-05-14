package it.niedermann.nextcloud.tables.ui.table.view.holder.type.number;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.BuildConfig;
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
            setStars(0);
        } else {
            final var value = data.getValue();
            try {
                final var stars = TextUtils.isEmpty(value) ? 0 : Integer.parseInt(data.getValue());
                setStars(stars);
            } catch (NumberFormatException e) {
                setStars(0);
                if (BuildConfig.DEBUG) {
                    throw new IllegalArgumentException("Could not parse stars: " + value);
                }
            }
        }
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
