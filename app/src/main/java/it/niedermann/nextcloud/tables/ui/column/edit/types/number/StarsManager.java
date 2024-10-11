package it.niedermann.nextcloud.tables.ui.column.edit.types.number;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Range;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.ManageNumberStarsBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;

public class StarsManager extends ColumnEditView<ManageNumberStarsBinding> {

    private boolean enabled = false;

    public StarsManager(@NonNull Context context) {
        super(context);
    }

    public StarsManager(@NonNull Context context,
                        @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StarsManager(@NonNull Context context,
                        @Nullable FragmentManager fragmentManager) {
        super(context, ManageNumberStarsBinding.inflate(LayoutInflater.from(context)), fragmentManager);

        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var star = i + 1;
            binding.stars.getChildAt(i).setOnClickListener(v -> {
                if (enabled) {
                    setValue(star);
                }
            });
        }
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        final var value = Optional.ofNullable(fullColumn.getColumn())
                .map(Column::getDefaultValue)
                .map(Value::getDoubleValue)
                .map(Math::ceil)
                .map(Double::intValue)
                .orElse(0);

        // https://github.com/nextcloud/tables/issues/1385
        final var validRange = new Range<>(1, 5);
        if (validRange.contains(value)) {
            setValue(value);
        }
    }

    private void setValue(int stars) {
        fullColumn.getColumn().getDefaultValue().setDoubleValue((double) stars);

        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var imageButton = (ImageButton) binding.stars.getChildAt(i);
            imageButton.setImageResource(i < stars
                    ? R.drawable.baseline_star_24
                    : R.drawable.baseline_star_border_24);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
    }
}
