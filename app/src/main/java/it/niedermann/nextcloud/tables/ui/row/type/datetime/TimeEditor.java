package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.time.Instant;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

public class TimeEditor extends TextEditor {

    public TimeEditor(@NonNull Context context) {
        super(context);
    }

    public TimeEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeEditor(@NonNull Context context,
                      @Nullable FragmentManager fragmentManager,
                      @NonNull Column column,
                      @Nullable Object value) {
        super(context, fragmentManager, column, value);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        final var view = super.onCreate(context);

        if (column.getDatetimeDefault() != null) {
            binding.editText.setText(String.valueOf(column.getDatetimeDefault()));
        }

        binding.getRoot().setStartIconDrawable(R.drawable.baseline_access_time_24);

        return view;
    }

    @Nullable
    @Override
    public Instant getValue() {
        return Instant.now(); // TODO
    }
}
