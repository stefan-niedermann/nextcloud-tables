package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Range;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

public class NumberEditor extends TextEditor {

    public NumberEditor(@NonNull Context context) {
        super(context);
    }

    public NumberEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberEditor(@NonNull Context context,
                        @Nullable FragmentManager fragmentManager,
                        @NonNull Column column,
                        @Nullable Object value) {
        super(context, fragmentManager, column, value);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @Nullable Object value) {
        final var view = super.onCreate(context, value);

        binding.editText.setInputType(InputType.TYPE_CLASS_NUMBER);

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

            // TODO check decimals
            // TODO check required?
            if (TextUtils.isEmpty(stringVal)) {
                return Optional.empty();
            }

            final var val = Long.parseLong(stringVal);
            return Range.create(column.getNumberMin(), column.getNumberMax()).contains(val)
                    ? Optional.empty()
                    : Optional.of(getContext().getString(R.string.validation_number_range, column.getNumberMin(), column.getNumberMax()));
        } catch (NumberFormatException e) {
            return Optional.of(getContext().getString(R.string.validation_number_not_parsable));
        }
    }
}
