package it.niedermann.nextcloud.tables.ui.row.edit.type.number;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Range;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.ui.row.edit.type.text.TextEditor;

public class NumberEditor extends TextEditor {

    private final NumberFormat format = NumberFormat.getInstance(Locale.US);
    private final NumberAttributes attributes;
    private final int numberDecimals;

    public NumberEditor(@NonNull Context context) {
        super(context);
        attributes = null;
        numberDecimals = 0;
    }

    public NumberEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        attributes = null;
        numberDecimals = 0;
    }

    public NumberEditor(@NonNull Context context,
                        @NonNull Column column,
                        @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);

        attributes = Objects.requireNonNull(column.getNumberAttributes());
        numberDecimals = attributes.numberDecimals();

        final int inputType = numberDecimals > 0
                ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_NUMBER;

        binding.editText.setInputType(inputType);
        binding.getRoot().setPrefixText(attributes.numberPrefix());
        binding.getRoot().setSuffixText(attributes.numberSuffix());
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_numbers_24);

        if (fragmentManager == null) {
            binding.getRoot().setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
            binding.getRoot().setEndIconDrawable(R.drawable.ic_outline_info_24);
            binding.getRoot().setEndIconOnClickListener(this::showFormatHintDialog);
        } else {
            binding.getRoot().setEndIconMode(TextInputLayout.END_ICON_NONE);
            binding.getRoot().setEndIconDrawable(null);
            binding.getRoot().setEndIconOnClickListener(null);
        }
    }

    private void showFormatHintDialog(@Nullable View eventSource) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.simple_hint)
                .setMessage(R.string.number_only_us_format)
                .setPositiveButton(R.string.simple_close, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    @Nullable
    public FullData getFullData() {
        final var min = attributes.numberMin();
        final var max = attributes.numberMax();

        final var value = Optional
                .ofNullable(binding.editText.getText())
                .map(Editable::toString)
                .flatMap(this::parseDouble)
                .map(val -> val < min ? min : val > max ? max : val)
                .orElse(null);

        Optional.ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(val -> val.setDoubleValue(value));

        return fullData;
    }

    private Optional<Double> parseDouble(@Nullable String value) {
        try {
            return Optional
                    .ofNullable(value)
                    .map(Double::parseDouble);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void setFullData(@NonNull FullData fullData) {
        this.fullData = fullData;

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(format::format)
                .orElse(null);

        binding.editText.setText(value);
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        try {
            final var stringVal = String.valueOf(binding.editText.getText());

            if (column.isMandatory() && TextUtils.isEmpty(stringVal)) {
                return Optional.of(getContext().getString(R.string.validation_mandatory));
            }

            if (TextUtils.isEmpty(stringVal)) {
                return Optional.empty();
            }

            // TODO check decimals

            final var number = format.parse(stringVal);

            if (number == null) {
                throw new NumberFormatException("Parsed number was null, but text was " + stringVal);
            }

            double val = number.doubleValue();

            final var min = attributes.numberMin();
            final var max = attributes.numberMax();
            final var df = new DecimalFormat();

            if (min != null && max != null) {
                return Range.create(min, max).contains(val)
                        ? Optional.empty()
                        : Optional.of(getContext().getString(R.string.validation_number_range, df.format(min), df.format(max)));
            } else if (min != null) {
                return val >= min
                        ? Optional.empty()
                        : Optional.of(getContext().getString(R.string.validation_number_min, df.format(min)));
            } else if (max != null) {
                return val <= max
                        ? Optional.empty()
                        : Optional.of(getContext().getString(R.string.validation_number_max, df.format(max)));
            } else {
                return Optional.empty();
            }

        } catch (Exception e) {
            return Optional.of(getContext().getString(R.string.validation_number_not_parsable));
        }
    }
}
