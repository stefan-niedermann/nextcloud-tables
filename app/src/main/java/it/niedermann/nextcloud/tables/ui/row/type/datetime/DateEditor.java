package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

public class DateEditor extends TextEditor {

    private static final String TAG = DateEditor.class.getSimpleName();
    private LocalDate value = LocalDate.now();

    public DateEditor(@NonNull Context context) {
        super(context);
    }

    public DateEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateEditor(@NonNull Context context,
                      @Nullable FragmentManager fragmentManager,
                      @NonNull Column column,
                      @NonNull Data data) {
        super(context, fragmentManager, column, data);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        final var view = super.onCreate(context, data);

        setValue(data.getValue());

        binding.getRoot().setStartIconDrawable(R.drawable.baseline_calendar_today_24);
        binding.editText.setOnClickListener(v -> {
            final var picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText(column.getTitle())
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            picker.addOnPositiveButtonClickListener(utcMilliseconds -> setValue(Instant.ofEpochMilli((Long) utcMilliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE)));
            picker.show(fragmentManager, DateEditor.class.getSimpleName());
        });

        binding.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.editText.callOnClick();
            }
        });

        return view;
    }

    @Override
    protected void setValue(@Nullable String value) {
        if (value == null) {
            this.value = null;
        } else {
            try {
                final var v = value.isBlank()
                        ? column.getDatetimeDefault()
                        : value;
                this.value = LocalDate.parse(v, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                Log.i(TAG, e.getMessage());
                this.value = null;
            }
        }

        if (this.value == null) {
            binding.editText.setText("");
        } else {
            final var renderedText = this.value.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            binding.editText.setText(renderedText);
        }
    }

    @Nullable
    @Override
    public String getValue() {
        return value.format(DateTimeFormatter.ISO_DATE);
    }
}
