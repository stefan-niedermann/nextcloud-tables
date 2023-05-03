package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.EditNumberStarsBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class StarsEditor extends ColumnEditView {

    @IntRange(from = 0, to = 5)
    protected int value = 0;
    protected EditNumberStarsBinding binding;

    public StarsEditor(@NonNull Context context) {
        super(context);
        binding = EditNumberStarsBinding.inflate(LayoutInflater.from(context));
    }

    public StarsEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditNumberStarsBinding.inflate(LayoutInflater.from(context));
    }

    public StarsEditor(@NonNull Context context,
                       @Nullable FragmentManager fragmentManager,
                       @NonNull Column column,
                       @Nullable Object value) {
        super(context, fragmentManager, column, value);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = EditNumberStarsBinding.inflate(LayoutInflater.from(context));

        binding.title.setText(column.getTitle());
        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var value = i;
            ((ImageButton) binding.stars.getChildAt(i)).setOnClickListener(v -> setValue(value));
        }

        final var defaultValue = column.getNumberDefault();
        setValue(defaultValue == null ? 0 : defaultValue.intValue());

        return binding.getRoot();
    }

    @Nullable
    @Override
    protected Object getValue() {
        return value;
    }

    @Override
    protected void setValue(@Nullable Object value) {
        if (value == null) {
            this.value = 0;
        } else {
            try {
                this.value = Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException e) {
                this.value = 0;
            }
        }
        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var imageButton = (ImageButton) binding.stars.getChildAt(i);
            imageButton.setImageResource(i <= this.value ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
        }
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        // TODO
    }
}
