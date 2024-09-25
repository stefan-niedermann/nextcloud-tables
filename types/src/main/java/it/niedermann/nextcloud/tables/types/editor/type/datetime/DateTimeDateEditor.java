package it.niedermann.nextcloud.tables.types.editor.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.type.text.TextEditor;

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
                              @Nullable Data data,
                              @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, fragmentManager, column, data, defaultValueSupplier);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        final var view = super.onCreate(context, data);

        final var value = data.getValue();
        if (value == null) {
            throw new IllegalStateException("value must not be null");
        }
        setValue(value);

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
            picker.addOnPositiveButtonClickListener(utcMilliseconds -> setValue(new JsonPrimitive(Instant.ofEpochMilli(utcMilliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE))));
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
    protected void setValue(@NonNull JsonElement value) {
        if (value.isJsonNull()) {
            this.value = null;
        } else {
            this.value = LocalDate.parse(value.getAsString(), DateTimeFormatter.ISO_DATE);
        }

        binding.editText.setText(this.value == null ? null : this.value.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
    }

    @NonNull
    @Override
    public JsonElement getValue() {
        return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value.format(DateTimeFormatter.ISO_DATE));
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (column.isMandatory() && getValue() == null) {
            return Optional.of(getContext().getString(R.string.validation_mandatory));
        }

        return Optional.empty();
    }
}
