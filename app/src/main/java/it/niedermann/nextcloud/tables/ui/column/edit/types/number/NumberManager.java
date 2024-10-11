package it.niedermann.nextcloud.tables.ui.column.edit.types.number;


import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageNumberBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;

public class NumberManager extends ColumnEditView<ManageNumberBinding> {

    public NumberManager(@NonNull Context context) {
        super(context);
    }

    public NumberManager(@NonNull Context context,
                         @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberManager(@NonNull Context context,
                         @Nullable FragmentManager fragmentManager) {
        super(context, ManageNumberBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        Optional.ofNullable(binding.numberDefault.getText())
                .map(String::valueOf)
                .map(Double::parseDouble)
                .ifPresent(fullColumn.getColumn().getDefaultValue()::setDoubleValue);

        fullColumn.getColumn().setNumberAttributes(new NumberAttributes(
                Optional.ofNullable(binding.numberMin.getText())
                        .map(Editable::toString)
                        .map(Double::parseDouble)
                        .orElse(null),
                Optional.ofNullable(binding.numberMax.getText())
                        .map(Editable::toString)
                        .map(Double::parseDouble)
                        .orElse(null),
                Optional.ofNullable(binding.numberDecimals.getText())
                        .map(Editable::toString)
                        .map(Integer::parseInt)
                        .orElse(null),
                Optional.ofNullable(binding.numberPrefix.getText())
                        .map(Editable::toString)
                        .orElse(null),
                Optional.ofNullable(binding.numberSuffix.getText())
                        .map(Editable::toString)
                        .orElse(null)
        ));

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        Optional.ofNullable(fullColumn.getColumn().getDefaultValue().getDoubleValue())
                .map(String::valueOf)
                .ifPresent(binding.numberDefault::setText);

        // FIXME String.valueOf(null) writes null into the inputs
        final var attributes = fullColumn.getColumn().getNumberAttributes();
        binding.numberMin.setText(String.valueOf(attributes.numberMin()));
        binding.numberMax.setText(String.valueOf(attributes.numberMax()));
        binding.numberDecimals.setText(String.valueOf(attributes.numberDecimals()));
        binding.numberPrefix.setText(attributes.numberPrefix());
        binding.numberSuffix.setText(attributes.numberSuffix());
    }
}
