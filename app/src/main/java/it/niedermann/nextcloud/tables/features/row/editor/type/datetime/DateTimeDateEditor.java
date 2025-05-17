package it.niedermann.nextcloud.tables.features.row.editor.type.datetime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

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
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextEditor;

public class DateTimeDateEditor extends TextEditor {

    @Nullable
    private LocalDate value;

    public DateTimeDateEditor(@NonNull Context context) {
        super(context);
    }

    public DateTimeDateEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeDateEditor(@NonNull Context context,
                              @Nullable FragmentManager fragmentManager,
                              @NonNull Column column) {
        super(context, column, fragmentManager);

        if (fragmentManager == null) {
            throw new IllegalArgumentException(FragmentManager.class.getSimpleName() + " is required for a " + DateTimeDateEditor.class.getSimpleName());
        }

        binding.getRoot().setStartIconDrawable(R.drawable.baseline_calendar_today_24);
        binding.editText.setOnClickListener(v -> {
            final var selection = this.value == null
                    ? MaterialDatePicker.todayInUtcMilliseconds()
                    : this.value.atTime(0, 0).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

            final var picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText(column.getTitle())
                    .setSelection(selection)
                    .build();

            picker.addOnPositiveButtonClickListener(utcMilliseconds -> setValue(
                    Instant.ofEpochMilli(utcMilliseconds)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()));

            picker.show(fragmentManager, DateTimeDateEditor.class.getSimpleName());
        });

        binding.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.editText.callOnClick();
            }
        });
    }

    @Override
    @Nullable
    public FullData getFullData() {
        Optional.ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(val -> val.setDateValue(value));

        return fullData;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void setFullData(@NonNull FullData fullData) {
        this.fullData = fullData;

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDateValue)
                .orElse(null);

        applyChangesWithoutChangingPristineState(() -> setValue(value));
    }

    protected void setValue(@Nullable LocalDate localDate) {
        this.value = localDate;

        final var value = Optional
                .ofNullable(localDate)
                .map(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)::format)
                .orElse(null);

        binding.editText.setText(value);
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (column.isMandatory() && this.value == null) {
            return Optional.of(getContext().getString(R.string.validation_mandatory));
        }

        return Optional.empty();
    }
}
