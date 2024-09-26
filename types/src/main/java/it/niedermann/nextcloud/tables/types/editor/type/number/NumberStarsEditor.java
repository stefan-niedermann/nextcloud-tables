package it.niedermann.nextcloud.tables.types.editor.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.databinding.EditNumberStarsBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;

public class NumberStarsEditor extends ColumnEditView {

    @IntRange(from = 0, to = 5)
    protected int value = 0;
    protected EditNumberStarsBinding binding;

    public NumberStarsEditor(@NonNull Context context) {
        super(context);
        binding = EditNumberStarsBinding.inflate(LayoutInflater.from(context));
    }

    public NumberStarsEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditNumberStarsBinding.inflate(LayoutInflater.from(context));
    }

    public NumberStarsEditor(@NonNull Context context,
                             @NonNull Column column,
                             @Nullable Data data,
                             @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, null, column, data, defaultValueSupplier);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditNumberStarsBinding.inflate(LayoutInflater.from(context));

        binding.title.setText(column.getTitle());
        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var star = i + 1;
            binding.stars.getChildAt(i).setOnClickListener(v -> setValue(new JsonPrimitive(star)));
        }

        final var value = data.getValue();
        if (value == null) {
            throw new IllegalStateException("value must not be null");
        }
        setValue(value);

        return binding.getRoot();
    }

    @NonNull
    @Override
    protected JsonElement getValue() {
        return new JsonPrimitive(value);
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        if (value.isJsonNull()) {
            this.value = 0;
        } else {
            try {
                this.value = value.getAsInt();
            } catch (NumberFormatException e) {
                this.value = 0;
            }
        }
        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var imageButton = (ImageButton) binding.stars.getChildAt(i);
            imageButton.setImageResource(i < this.value ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
        }
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        // TODO
    }
}
