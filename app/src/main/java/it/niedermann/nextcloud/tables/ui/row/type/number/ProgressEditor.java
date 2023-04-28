package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class ProgressEditor extends ColumnEditView implements Slider.OnChangeListener {

    @IntRange(from = 0, to = 5)
    protected int value = 0;
    protected Slider slider;

    public ProgressEditor(@NonNull Context context) {
        super(context);
    }

    public ProgressEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        slider = new Slider(context);
        slider.setValueFrom(0f);
        slider.setValueTo(100f);
        slider.setStepSize(1f);
        slider.addOnChangeListener(this);

        final var defaultValue = column.getNumberDefault();
        slider.setValue(defaultValue == null ? 0 : defaultValue.intValue());

        return slider;
    }

    @Nullable
    @Override
    protected Object getValue() {
        return (int) slider.getValue();
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
