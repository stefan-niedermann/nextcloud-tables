package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

@SuppressLint("ViewConstructor")
public class NumberEditor extends TextEditor {

    public NumberEditor(@NonNull Context context, @NonNull Column column) {
        this(context, null, column);
    }

    private NumberEditor(@NonNull Context context, @Nullable AttributeSet attrs, @NonNull Column column) {
        this(context, attrs, 0, column);
    }

    protected NumberEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, @NonNull Column column) {
        super(context, attrs, defStyleAttr, column);
        final var editText = getEditText();
        if (editText == null) {
            throw new IllegalStateException("Expected editText to be set by super class");
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (column.getNumberDefault() != null) {
            editText.setText(String.valueOf(column.getNumberDefault()));
        }
        setPrefixText(column.getNumberPrefix());
        setSuffixText(column.getNumberSuffix());
    }
}
