package it.niedermann.nextcloud.tables.types.editor.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.databinding.EditNumberProgressBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;

public class NumberProgressEditor extends ColumnEditView implements Slider.OnChangeListener {

    protected EditNumberProgressBinding binding;

    public NumberProgressEditor(@NonNull Context context) {
        super(context);
        binding = EditNumberProgressBinding.inflate(LayoutInflater.from(context));
    }

    public NumberProgressEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditNumberProgressBinding.inflate(LayoutInflater.from(context));
    }

    public NumberProgressEditor(@NonNull Context context,
                                @NonNull Column column,
                                @Nullable Data data,
                                @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, null, column, data, defaultValueSupplier);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditNumberProgressBinding.inflate(LayoutInflater.from(context));
        binding.title.setText(column.getTitle());
        binding.progress.setValueFrom(0f);
        binding.progress.setValueTo(100f);
        binding.progress.setStepSize(1f);
        binding.progress.addOnChangeListener(this);

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
        return new JsonPrimitive((int) binding.progress.getValue());
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        if (value.isJsonNull()) {
            binding.progress.setValue(0);
        } else {
            try {
                binding.progress.setValue(value.getAsInt());
            } catch (NumberFormatException e) {
                binding.progress.setValue(0);
            }
        }
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        // TODO
    }

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        onValueChanged();
    }
}
