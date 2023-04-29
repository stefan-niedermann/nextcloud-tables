package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.EditNumberProgressBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class ProgressEditor extends ColumnEditView implements Slider.OnChangeListener {

    @IntRange(from = 0, to = 5)
    protected int value = 0;
    protected EditNumberProgressBinding binding;

    public ProgressEditor(@NonNull Context context) {
        super(context);
        binding = EditNumberProgressBinding.inflate(LayoutInflater.from(context));
    }

    public ProgressEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditNumberProgressBinding.inflate(LayoutInflater.from(context));
    }

    public ProgressEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = EditNumberProgressBinding.inflate(LayoutInflater.from(context));
        binding.getRoot().setValueFrom(0f);
        binding.getRoot().setValueTo(100f);
        binding.getRoot().setStepSize(1f);
        binding.getRoot().addOnChangeListener(this);

        final var defaultValue = column.getNumberDefault();
        binding.getRoot().setValue(defaultValue == null ? 0 : defaultValue.intValue());

        return binding.getRoot();
    }

    @Nullable
    @Override
    protected Object getValue() {
        return (int) binding.getRoot().getValue();
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
