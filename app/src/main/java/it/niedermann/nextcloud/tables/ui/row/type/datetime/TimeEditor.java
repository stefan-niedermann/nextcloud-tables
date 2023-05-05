package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import static it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime.AbstractDateTimeCellViewHolder.DATETIME_NONE;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

public class TimeEditor extends TextEditor {

    private static final String TAG = TimeEditor.class.getSimpleName();
    private Instant value = Instant.now();

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

        if (column.getDatetimeDefault() != null) {
            binding.editText.setText(String.valueOf(column.getDatetimeDefault()));
        }

        binding.getRoot().setStartIconDrawable(R.drawable.baseline_calendar_today_24);
        binding.editText.setOnClickListener(v -> {
            final var localTime = LocalTime.ofInstant(getValue(), ZoneId.systemDefault());
            final var picker = new MaterialTimePicker.Builder()
                    .setTitleText(column.getTitle())
                    .setHour(localTime.getHour())
                    .setMinute(localTime.getMinute())
                    .build();
            picker.addOnPositiveButtonClickListener(value1 -> {
                final var instant = localTime
                        .atDate(LocalDate.now())
                        .atZone(ZoneId.systemDefault())
                        .toInstant();
                setValue(instant);
            });
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
    protected void setValue(@Nullable Object value) {
        if (value == null) {
            this.value = null;
        } else if (value instanceof Instant) {
            this.value = (Instant) value;
        } else if (value instanceof Long) {
            this.value = Instant.ofEpochMilli((Long) value);
        } else if (value instanceof String) {
            try {
                final var v = TextUtils.isEmpty((String) value) || DATETIME_NONE.equals(value)
                        ? column.getDatetimeDefault()
                        : (String) value;
                this.value = LocalDateTime.parse(v, DateTimeFormatter.ISO_TIME).atZone(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException e) {
                Log.i(TAG, e.getMessage());
//                this.value =
//                binding.data.setText(column.getDatetimeDefault());
            }
        } else {
            this.value = null;
        }

        if (this.value == null) {
            binding.editText.setText("");
        } else {
            final var renderedText = this.value.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
            binding.editText.setText(renderedText);
        }
    }

    @Nullable
    @Override
    public Instant getValue() {
        return value;
    }
}
