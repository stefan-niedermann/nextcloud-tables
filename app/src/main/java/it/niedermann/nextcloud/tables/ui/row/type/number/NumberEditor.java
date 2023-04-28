package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Range;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public NumberEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        final var view = super.onCreate(context);

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (column.getNumberDefault() != null) {
            editText.setText(String.valueOf(column.getNumberDefault()));
        }

        textInputLayout.setPrefixText(column.getNumberPrefix());
        textInputLayout.setSuffixText(column.getNumberSuffix());

        return view;
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        try {
            final var stringVal = String.valueOf(editText.getText());

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
