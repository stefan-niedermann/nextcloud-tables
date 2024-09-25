package it.niedermann.nextcloud.tables.types.editor.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.databinding.EditDatetimeBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.ColumnEditView;

public class DateTimeEditor extends ColumnEditView {

    protected EditDatetimeBinding binding;
    protected DateTimeDateEditor date;
    protected DateTimeTimeEditor time;

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
                          @Nullable Data data,
                          @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, fragmentManager, column, data, defaultValueSupplier);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditDatetimeBinding.inflate(LayoutInflater.from(context));

        final var values = extractValues(data.getValue());

        final var dateData = new Data(data);
        final var timeData = new Data(data);

        dateData.setValue(values.first == null ? JsonNull.INSTANCE : new JsonPrimitive(values.first.format(DateTimeFormatter.ISO_DATE)));
        timeData.setValue(values.second == null ? JsonNull.INSTANCE : new JsonPrimitive(values.second.format(DateTimeFormatter.ISO_TIME)));

        try {
            date = new DateTimeDateEditor(context, fragmentManager, column, dateData, defaultValueSupplier);
            time = new DateTimeTimeEditor(context, fragmentManager, column, timeData, defaultValueSupplier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final var horizontalMargin = context.getResources().getDimensionPixelSize(R.dimen.spacer_1x);
        final var dateLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        dateLayoutParams.setMarginEnd(horizontalMargin);
        final var timeLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        timeLayoutParams.setMarginStart(horizontalMargin);

        date.setLayoutParams(dateLayoutParams);
        time.setLayoutParams(timeLayoutParams);

        binding.title.setText(column.getTitle());
        binding.dateAndTimePickers.addView(date);
        binding.dateAndTimePickers.addView(time);

        setValue(data.getValue());

        return binding.getRoot();
    }

    @NonNull
    @Override
    protected JsonElement getValue() {
        final var date = this.date.getValue();
        final var time = this.time.getValue();

        if (date.isJsonNull()) {
            return JsonNull.INSTANCE;

        } else if (time.isJsonNull()) {
            final var localDate = LocalDate.parse(date.getAsString(), DateTimeFormatter.ISO_DATE);
            return new JsonPrimitive(localDate.atTime(0, 0).format(DateTimeFormatter.ISO_DATE_TIME));

        } else {
            final var localDate = LocalDate.parse(date.getAsString(), DateTimeFormatter.ISO_DATE);
            final var localTime = LocalTime.parse(time.getAsString(), DateTimeFormatter.ISO_TIME);
            return new JsonPrimitive(localDate.atTime(localTime).format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        final var values = extractValues(value);
        date.setValue(values.first == null ? JsonNull.INSTANCE : new JsonPrimitive(values.first.format(DateTimeFormatter.ISO_DATE)));
        time.setValue(values.second == null ? JsonNull.INSTANCE : new JsonPrimitive(values.second.format(DateTimeFormatter.ISO_TIME)));
    }

    private Pair<LocalDate, LocalTime> extractValues(@NonNull JsonElement value) {
        if (value.isJsonNull()) {
            return new Pair<>(null, null);
        } else {
            final var dateTime = LocalDateTime.parse(value.getAsString(), DateTimeFormatter.ISO_DATE_TIME);
            return new Pair<>(dateTime.toLocalDate(), dateTime.toLocalTime());
        }
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (this.time.getValue() == null) {
            return Optional.of(getContext().getString(R.string.validation_time_missing));

        } else if (column.isMandatory() && getValue() == null) {
            return Optional.of(getContext().getString(R.string.validation_mandatory));

        }

        return Optional.empty();
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        this.time.setErrorMessage(message);
    }
}
