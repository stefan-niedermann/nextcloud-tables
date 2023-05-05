package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;

public class TextLineEditor extends TextEditor {

    public TextLineEditor(@NonNull Context context) {
        super(context);
    }

    public TextLineEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLineEditor(@NonNull Context context,
                          @Nullable FragmentManager fragmentManager,
                          @NonNull Column column,
                          @Nullable Object value) {
        super(context, fragmentManager, column, value);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @Nullable Object value) {
        final var view = super.onCreate(context, value);
        binding.editText.setMaxLines(1);
        return view;
    }
}
