package it.niedermann.nextcloud.tables.features.column.edit.types.number;


import static java.util.function.Predicate.not;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.ManageNumberBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;

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
                .filter(not(String::isBlank))
                .map(Double::parseDouble)
                .ifPresentOrElse(
                        fullColumn.getColumn().getDefaultValue()::setDoubleValue,
                        () -> fullColumn.getColumn().getDefaultValue().setDoubleValue(null));

        fullColumn.getColumn().setNumberAttributes(new NumberAttributes(
                Optional.ofNullable(binding.numberMin.getText())
                        .map(Editable::toString)
                        .filter(not(String::isBlank))
                        .map(Double::parseDouble)
                        .orElse(null),
                Optional.ofNullable(binding.numberMax.getText())
                        .map(Editable::toString)
                        .filter(not(String::isBlank))
                        .map(Double::parseDouble)
                        .orElse(null),
                Optional.ofNullable(binding.numberDecimals.getText())
                        .map(Editable::toString)
                        .filter(not(String::isBlank))
                        .map(Integer::parseInt)
                        .orElse(null),
                Optional.ofNullable(binding.numberPrefix.getText())
                        .map(Editable::toString)
                        .filter(not(String::isBlank))
                        .orElse(null),
                Optional.ofNullable(binding.numberSuffix.getText())
                        .map(Editable::toString)
                        .filter(not(String::isBlank))
                        .orElse(null)
        ));

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        final var column = Optional.of(fullColumn.getColumn());
        final var attributes = column.map(Column::getNumberAttributes);

        binding.numberDefault.setText(column.map(Column::getDefaultValue).map(Value::getDoubleValue).map(String::valueOf).orElse(null));
        binding.numberMin.setText(attributes.map(NumberAttributes::numberMin).map(String::valueOf).orElse(null));
        binding.numberMax.setText(attributes.map(NumberAttributes::numberMax).map(String::valueOf).orElse(null));
        binding.numberDecimals.setText(attributes.map(NumberAttributes::numberDecimals).map(String::valueOf).orElse(null));
        binding.numberPrefix.setText(attributes.map(NumberAttributes::numberPrefix).orElse(null));
        binding.numberSuffix.setText(attributes.map(NumberAttributes::numberSuffix).orElse(null));
    }
}
