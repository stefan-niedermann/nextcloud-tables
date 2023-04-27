package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;

@SuppressLint("ViewConstructor")
public class TextLineEditor extends TextEditor {

    public TextLineEditor(@NonNull Context context, @NonNull Column column) {
        this(context, null, column);
    }

    private TextLineEditor(@NonNull Context context, @Nullable AttributeSet attrs, @NonNull Column column) {
        this(context, attrs, 0, column);
    }

    protected TextLineEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, @NonNull Column column) {
        super(context, attrs, defStyleAttr, column);
        final var editText = getEditText();
        if (editText == null) {
            throw new IllegalStateException("Expected editText to be set by super class");
        }
        editText.setMaxLines(1);
    }
}
