package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

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
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;

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
            final var selection = value == null ? LocalTime.now() : value;

            final var picker = new MaterialTimePicker.Builder()
                    .setTitleText(column.getTitle())
                    .setHour(selection.getHour())
                    .setMinute(selection.getMinute())
                    .build();
            picker.addOnPositiveButtonClickListener(v1 -> setValue(LocalTime.of(picker.getHour(), picker.getMinute()).format(DateTimeFormatter.ISO_TIME)));
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
    protected void setValue(@Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            this.value = null;
        } else {
            this.value = LocalTime.parse(value, DateTimeFormatter.ISO_TIME);
        }

        binding.editText.setText(this.value == null ? null : this.value.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
    }

    @Nullable
    @Override
    public String getValue() {
        return value == null ? null : value.format(DateTimeFormatter.ISO_TIME);
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
    protected void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }
}
