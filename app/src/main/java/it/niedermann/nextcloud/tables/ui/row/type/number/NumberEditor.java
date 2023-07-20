package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Range;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

public class NumberEditor extends TextEditor {

    public NumberEditor(@NonNull Context context) {
        super(context);
    }

    public NumberEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberEditor(@NonNull Context context,
                        @NonNull Column column,
                        @NonNull Data data) {
        super(context, null, column, data);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        final var view = super.onCreate(context, data);

        final var decimals = column.getNumberDecimals();
        final int inputType = decimals != null && decimals > 0
                ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_NUMBER;
        binding.editText.setInputType(inputType);

        binding.getRoot().setPrefixText(column.getNumberPrefix());
        binding.getRoot().setSuffixText(column.getNumberSuffix());
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_numbers_24);

        return view;
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

            final var val = Double.parseDouble(stringVal);
            final var min = column.getNumberMin();
            final var max = column.getNumberMax();
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

        } catch (NumberFormatException e) {
            return Optional.of(getContext().getString(R.string.validation_number_not_parsable));
        }
    }
}
