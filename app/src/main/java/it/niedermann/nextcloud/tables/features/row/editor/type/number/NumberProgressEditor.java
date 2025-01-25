package it.niedermann.nextcloud.tables.features.row.editor.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.EditNumberProgressBinding;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;

public class NumberProgressEditor extends DataEditView<EditNumberProgressBinding> implements Slider.OnChangeListener {


    public NumberProgressEditor(@NonNull Context context) {
        super(context, EditNumberProgressBinding.inflate(LayoutInflater.from(context)));
    }

    public NumberProgressEditor(@NonNull Context context,
                                @Nullable AttributeSet attrs) {
        super(context, attrs, EditNumberProgressBinding.inflate(LayoutInflater.from(context)));
    }

    public NumberProgressEditor(@NonNull Context context,
                                @NonNull Column column) {
        super(context, EditNumberProgressBinding.inflate(LayoutInflater.from(context)), column);

        binding.title.setText(column.getTitle());
        binding.progress.addOnChangeListener(this);
    }

    @Override
    @Nullable
    public FullData getFullData() {
        final var value = Optional
                .of(binding.progress.getValue())
                .map(Double::valueOf)
                .orElse(null);

        Optional.ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(val -> val.setDoubleValue(value));

        return fullData;
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        final var attributes = Optional
                .of(column.getNumberAttributes());

        final var min = attributes
                .map(NumberAttributes::numberMin)
                .map(Double::floatValue)
                .orElse(0f);

        final var max = attributes
                .map(NumberAttributes::numberMax)
                .map(Double::floatValue)
                .orElse(100f);

        final float stepSize = (max - min) / 100f;

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(Double::floatValue)
                .map(val -> val < min ? min : val > max ? max : val)
                .map(val -> (Math.round(val / stepSize)) * stepSize)
                .orElse(min);

        binding.progress.setValueFrom(min);
        binding.progress.setValueTo(max);
        binding.progress.setStepSize(stepSize);
        binding.progress.setValue(value);
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
