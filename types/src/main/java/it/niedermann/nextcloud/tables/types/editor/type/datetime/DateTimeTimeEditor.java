package it.niedermann.nextcloud.tables.types.editor.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.type.text.TextEditor;

public class DateTimeTimeEditor extends TextEditor {

    private LocalTime value;

    public DateTimeTimeEditor(@NonNull Context context) {
        super(context);
    }

    public DateTimeTimeEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeTimeEditor(@NonNull Context context,
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
            final var selection = this.value == null ? LocalTime.now() : this.value;

            final var picker = new MaterialTimePicker.Builder()
                    .setTitleText(column.getTitle())
                    .setHour(selection.getHour())
                    .setMinute(selection.getMinute())
                    .build();
            picker.addOnPositiveButtonClickListener(v1 -> setValue(new JsonPrimitive(LocalTime.of(picker.getHour(), picker.getMinute()).format(DateTimeFormatter.ISO_TIME))));
            picker.show(fragmentManager, DateTimeDateEditor.class.getSimpleName());
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
    protected void setValue(@NonNull JsonElement value) {
        if (value.isJsonNull()) {
            this.value = null;
        } else {
            this.value = LocalTime.parse(value.getAsString(), DateTimeFormatter.ISO_TIME);
        }

        binding.editText.setText(this.value == null ? null : this.value.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
    }

    @NonNull
    @Override
    public JsonElement getValue() {
        return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value.format(DateTimeFormatter.ISO_TIME));
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (column.isMandatory() && getValue() == null) {
            return Optional.of(getContext().getString(R.string.validation_mandatory));
        }

        return super.validate();
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }
}
