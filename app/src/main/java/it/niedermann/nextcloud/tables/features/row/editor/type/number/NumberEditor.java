package it.niedermann.nextcloud.tables.features.row.editor.type.number;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Range;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextEditor;

public class NumberEditor extends TextEditor {

    private NumberAttributes attributes;
    private NumberFormat format;

    public NumberEditor(@NonNull Context context) {
        super(context);
    }

    public NumberEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberEditor(@NonNull Context context,
                        @NonNull Column column,
                        @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);

        attributes = requireNonNull(column.getNumberAttributes());
        format = getNumberFormat(attributes);

        binding.getRoot().setPrefixText(attributes.numberPrefix());
        binding.getRoot().setSuffixText(attributes.numberSuffix());
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_numbers_24);

        /// `true` if [NumberAttributes#numberMin] is defined and the minimum is negative
        final var signed = Optional
                .ofNullable(attributes.numberMin())
                .map(numberMin -> numberMin < 0);

        /// `true` if [NumberAttributes#numberDecimals] is defined and greater than `0`
        final var decimal = Optional
                .ofNullable(attributes.numberDecimals())
                .map(numberDecimals -> numberDecimals > 0);

        binding.editText.setKeyListener(DigitsKeyListener.getInstance(
                Locale.getDefault(),
                signed.orElse(false),
                decimal.orElse(false)));

        binding.editText.setRawInputType(
                InputType.TYPE_CLASS_NUMBER |
                signed
                        .map(s -> EditorInfo.TYPE_NUMBER_FLAG_SIGNED)
                        .orElse(0) |
                decimal
                        .map(d -> EditorInfo.TYPE_NUMBER_FLAG_DECIMAL)
                        .orElse(0));
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final var bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putSerializable("attributes", attributes);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@Nullable Parcelable state) {
        if (state instanceof Bundle bundle) {
            attributes = (NumberAttributes) bundle.getSerializable("attributes");
            format = getNumberFormat(requireNonNull(attributes));
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        }
    }

    @Override
    @Nullable
    public FullData getFullData() {
        final var attributes = Optional.of(this.attributes);
        final var min = attributes.map(NumberAttributes::numberMin).orElse(Double.MIN_VALUE);
        final var max = attributes.map(NumberAttributes::numberMax).orElse(Double.MAX_VALUE);

        final var value = Optional
                .ofNullable(binding.editText.getText())
                .map(Editable::toString)
                .filter(not(String::isBlank))
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
        return Optional
                .ofNullable(value)
                .filter(not(String::isBlank))
                .map(source -> {
                    try {
                        return format.parse(source);
                    } catch (ParseException e) {
                        return null;
                    }
                })
                .map(Number::doubleValue);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void setFullData(@NonNull FullData fullData) {
        this.fullData = fullData;

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .flatMap(doubleValue -> Optional
                        .ofNullable(attributes.numberDecimals())
                        .map(decimals -> "%." + decimals + "f")
                        .map(formatString -> String.format(Locale.getDefault(), formatString, doubleValue)));

        applyChangesWithoutChangingPristineState(() -> binding.editText.setText(value.orElse(null)));
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

    private NumberFormat getNumberFormat(@NonNull NumberAttributes attributes) {
        return Optional.of(attributes)
                .map(NumberAttributes::numberDecimals)
                .filter(decimals -> decimals > 0)
                .map(decimals -> NumberFormat.getInstance())
                .orElseGet(NumberFormat::getIntegerInstance);
    }
}
