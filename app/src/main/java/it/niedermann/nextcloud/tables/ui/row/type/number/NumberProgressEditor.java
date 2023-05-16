package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditNumberProgressBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

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
                                @NonNull Data data) {
        super(context, null, column, data);
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

        setValue(data.getValue());

        return binding.getRoot();
    }

    @Nullable
    @Override
    protected String getValue() {
        return String.valueOf((int) binding.progress.getValue());
    }

    @Override
    protected void setValue(@Nullable String value) {
        if (value == null) {
            binding.progress.setValue(0);
        } else {
            try {
                binding.progress.setValue(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                binding.progress.setValue(0);
            }
        }
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        // TODO
    }

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        onValueChanged();
    }
}
