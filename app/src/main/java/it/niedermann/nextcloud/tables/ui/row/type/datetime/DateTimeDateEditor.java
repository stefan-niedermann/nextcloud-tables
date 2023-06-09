package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

public class DateTimeDateEditor extends TextEditor {

    private LocalDate value;

    public DateTimeDateEditor(@NonNull Context context) {
        super(context);
    }

    public DateTimeDateEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeDateEditor(@NonNull Context context,
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
            final var selection = value == null
                    ? MaterialDatePicker.todayInUtcMilliseconds()
                    : value.atTime(0, 0).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

            final var picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText(column.getTitle())
                    .setSelection(selection)
                    .build();
            picker.addOnPositiveButtonClickListener(utcMilliseconds -> setValue(Instant.ofEpochMilli((Long) utcMilliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE)));
            picker.show(fragmentManager, DateTimeDateEditor.class.getSimpleName());
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
        if (TextUtils.isEmpty(value)) {
            this.value = null;
        } else {
            this.value = LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
        }

        binding.editText.setText(this.value == null ? null : this.value.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
    }

    @Nullable
    @Override
    public String getValue() {
        return value == null ? null : value.format(DateTimeFormatter.ISO_DATE);
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (column.isMandatory() && getValue() == null) {
            return Optional.of(getContext().getString(R.string.validation_mandatory));
        }

        return Optional.empty();
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }
}
