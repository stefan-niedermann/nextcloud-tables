package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;

public class TextLineEditor extends TextEditor {

    public TextLineEditor(@NonNull Context context) {
        super(context);
    }

    public TextLineEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLineEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        final var view = super.onCreate(context);
        binding.editText.setMaxLines(1);
        return view;
    }
}
