package it.niedermann.nextcloud.tables.features.row.editor.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.EditNumberStarsBinding;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;

public class NumberStarsEditor extends DataEditView<EditNumberStarsBinding> implements Consumer<Integer> {

    public NumberStarsEditor(@NonNull Context context) {
        super(context, EditNumberStarsBinding.inflate(LayoutInflater.from(context)));
    }

    public NumberStarsEditor(@NonNull Context context,
                             @Nullable AttributeSet attrs) {
        super(context, attrs, EditNumberStarsBinding.inflate(LayoutInflater.from(context)));
    }

    public NumberStarsEditor(@NonNull Context context,
                             @NonNull Column column) {
        super(context, EditNumberStarsBinding.inflate(LayoutInflater.from(context)), column);

        binding.title.setText(column.getTitle());
        binding.stars.setValueChangedListener(this);
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(Math::round)
                .map(Long::intValue)
                .orElse(0);

        binding.stars.setValue(value);
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        // TODO
    }

    @Override
    public void accept(Integer newValue) {
        if (newValue == null) {
            throw new NullPointerException("newValue must not be null for stars.");
        }

        Optional
                .ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(value -> value.setDoubleValue(newValue.doubleValue()));

        onValueChanged();
    }
}
