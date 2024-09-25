package it.niedermann.nextcloud.tables.types.editor.type.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class TextLineEditor extends TextEditor {

    public TextLineEditor(@NonNull Context context) {
        super(context);
    }

    public TextLineEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLineEditor(@NonNull Context context,
                          @NonNull Column column,
                          @Nullable Data data,
                          @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, null, column, data, defaultValueSupplier);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        final var view = super.onCreate(context, data);
        binding.editText.setMaxLines(1);
        return view;
    }
}
