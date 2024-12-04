package it.niedermann.nextcloud.tables.ui.table.view.types.viewholder.number;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.shared.config.FeatureToggle;
import it.niedermann.nextcloud.tables.ui.table.view.types.CellViewHolder;

public class StarsCellViewHolder extends CellViewHolder {

    private final TableviewCellStarsBinding binding;

    public StarsCellViewHolder(@NonNull TableviewCellStarsBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData, @NonNull Column column) {
        try {
            final var stars = Optional
                    .of(fullData)
                    .map(FullData::getData)
                    .map(Data::getValue)
                    .map(Value::getDoubleValue)
                    .map(Math::round)
                    .map(Long::intValue)
                    .orElse(0);
            setStars(stars);
        } catch (NumberFormatException e) {
            setStars(0);
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }
        }
    }

    private void setStars(int count) {
        for (int i = 0; i < 5; i++) {
            final var child = binding.cellContainer.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setImageResource(i < count ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
            } else {
                if (FeatureToggle.STRICT_MODE.enabled) {
                    throw new IllegalStateException("Expected child at position " + i + " to be of type " + ImageView.class.getSimpleName() + " but was " + child.getClass().getSimpleName());
                }
            }
        }
    }
}
