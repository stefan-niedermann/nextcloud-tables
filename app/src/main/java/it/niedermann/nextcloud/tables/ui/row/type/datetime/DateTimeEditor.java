package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.time.Instant;
import java.time.ZoneId;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.EditDatetimeBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class DateTimeEditor extends ColumnEditView {

    protected EditDatetimeBinding binding;

    public DateTimeEditor(@NonNull Context context) {
        super(context);
        binding = EditDatetimeBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot());
    }

    public DateTimeEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditDatetimeBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot());
    }

    public DateTimeEditor(@NonNull Context context,
                          @Nullable FragmentManager fragmentManager,
                          @NonNull Column column,
                          @Nullable Object value) {
        super(context, fragmentManager, column, value);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = EditDatetimeBinding.inflate(LayoutInflater.from(context));

//        binding.date.setHint(column.getTitle());
//        binding.time.setStartIconDrawable(R.drawable.baseline_short_text_24);

        return binding.getRoot();
    }

    @Nullable
    @Override
    protected Instant getValue() {
        final var date = binding.date.getValue();
        final var time = binding.time.getValue();

        if (date == null || time == null) {
            return null;
        }

        final var localDate = date.atZone(ZoneId.systemDefault()).toLocalDate();
        final var localTime = time.atZone(ZoneId.systemDefault()).toLocalTime();

        return Instant.from(localDate.atTime(localTime));
    }

    @Override
    protected void setValue(@Nullable Object value) {

    }

    @Override
    protected void setErrorMessage(@Nullable String message) {

    }
}
