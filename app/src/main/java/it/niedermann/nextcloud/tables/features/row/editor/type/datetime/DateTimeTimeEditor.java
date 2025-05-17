package it.niedermann.nextcloud.tables.features.row.editor.type.datetime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextEditor;

public class DateTimeTimeEditor extends TextEditor {

    @Nullable
    private LocalTime value;

    public DateTimeTimeEditor(@NonNull Context context) {
        super(context);
    }

    public DateTimeTimeEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeTimeEditor(@NonNull Context context,
                              @Nullable FragmentManager fragmentManager,
                              @NonNull Column column) {
        super(context, column, fragmentManager);

        if (fragmentManager == null) {
            throw new IllegalArgumentException(FragmentManager.class.getSimpleName() + " is required for a " + DateTimeTimeEditor.class.getSimpleName());
        }

        binding.getRoot().setStartIconDrawable(R.drawable.baseline_calendar_today_24);
        binding.editText.setOnClickListener(v -> {
            final var selection = this.value == null ? LocalTime.now() : this.value;

            final var picker = new MaterialTimePicker.Builder()
                    .setTitleText(column.getTitle())
                    .setHour(selection.getHour())
                    .setMinute(selection.getMinute())
                    .build();

            picker.addOnPositiveButtonClickListener(v1 -> setValue(
                    LocalTime.of(picker.getHour(), picker.getMinute())));

            picker.show(fragmentManager, DateTimeDateEditor.class.getSimpleName());
        });

        binding.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.editText.callOnClick();
            }
        });

        binding.getRoot().setStartIconDrawable(R.drawable.baseline_access_time_24);
    }

    @Override
    @Nullable
    public FullData getFullData() {
        Optional.ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(val -> val.setTimeValue(value));

        return fullData;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void setFullData(@NonNull FullData fullData) {
        this.fullData = fullData;

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getTimeValue)
                .orElse(null);

        applyChangesWithoutChangingPristineState(() -> setValue(value));
    }

    protected void setValue(@Nullable LocalTime localTime) {
        this.value = localTime;

        final var value = Optional
                .ofNullable(localTime)
                .map(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)::format)
                .orElse(null);

        binding.editText.setText(value);
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (column.isMandatory() && this.value == null) {
            return Optional.of(getContext().getString(R.string.validation_mandatory));
        }

        return super.validate();
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }
}
