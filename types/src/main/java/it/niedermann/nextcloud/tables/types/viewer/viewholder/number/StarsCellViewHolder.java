package it.niedermann.nextcloud.tables.types.viewer.viewholder.number;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.BuildConfig;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class StarsCellViewHolder extends CellViewHolder {

    private final TableviewCellStarsBinding binding;

    public StarsCellViewHolder(@NonNull TableviewCellStarsBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        if (value.isJsonNull()) {
            setStars(0);
        } else {
            try {
                final var stars = value.getAsInt();
                setStars(stars);
            } catch (NumberFormatException e) {
                setStars(0);
                if (BuildConfig.DEBUG) {
                    throw e;
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
                if (BuildConfig.DEBUG) {
                    throw new IllegalStateException("Expected child at position " + i + " to be of type " + ImageView.class.getSimpleName() + " but was " + child.getClass().getSimpleName());
                }
            }
        }
    }
}
