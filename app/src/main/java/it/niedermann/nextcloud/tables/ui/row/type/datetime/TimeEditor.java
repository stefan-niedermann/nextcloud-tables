package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

public class TimeEditor extends TextEditor {

    private static final String TAG = TimeEditor.class.getSimpleName();
    private LocalTime value = LocalTime.now();

    public TimeEditor(@NonNull Context context) {
        super(context);
    }

    public TimeEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeEditor(@NonNull Context context,
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
            final var picker = new MaterialTimePicker.Builder()
                    .setTitleText(column.getTitle())
                    .setHour(value.getHour())
                    .setMinute(value.getMinute())
                    .build();
            picker.addOnPositiveButtonClickListener(v1 -> setValue(LocalTime.of(picker.getHour(), picker.getMinute()).format(DateTimeFormatter.ISO_TIME)));
            picker.show(fragmentManager, DateEditor.class.getSimpleName());
        });

        binding.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.editText.callOnClick();
            }
        });

        binding.getRoot().setStartIconDrawable(R.drawable.baseline_access_time_24);

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
                this.value = LocalTime.parse(v, DateTimeFormatter.ISO_TIME);
            } catch (DateTimeParseException e) {
                Log.i(TAG, e.getMessage());
                this.value = null;
            }
        }

        if (this.value == null) {
            binding.editText.setText("");
        } else {
            final var renderedText = this.value.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
            binding.editText.setText(renderedText);
        }
    }

    @Nullable
    @Override
    public String getValue() {
        return value.format(DateTimeFormatter.ISO_TIME);
    }
}
