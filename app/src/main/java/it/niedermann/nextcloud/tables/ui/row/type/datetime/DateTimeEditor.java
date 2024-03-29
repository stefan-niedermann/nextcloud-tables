package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditDatetimeBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

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
                          @NonNull Data data) {
        super(context, fragmentManager, column, data);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditDatetimeBinding.inflate(LayoutInflater.from(context));

        final var values = extractValues(data.getValue());

        final var dateData = new Data(data);
        final var timeData = new Data(data);

        dateData.setValue(values.first == null ? null : values.first.format(DateTimeFormatter.ISO_DATE));
        timeData.setValue(values.second == null ? null : values.second.format(DateTimeFormatter.ISO_TIME));

        date = new DateTimeDateEditor(context, fragmentManager, column, dateData);
        time = new DateTimeTimeEditor(context, fragmentManager, column, timeData);

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

    @Nullable
    @Override
    protected String getValue() {
        final var date = this.date.getValue();
        final var time = this.time.getValue();

        if (date == null) {
            return null;

        } else if (time == null) {
            final var localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return localDate.atTime(0, 0).format(DateTimeFormatter.ISO_DATE_TIME);

        } else {
            final var localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            final var localTime = LocalTime.parse(time, DateTimeFormatter.ISO_TIME);
            return localDate.atTime(localTime).format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    @Override
    protected void setValue(@Nullable String value) {
        final var values = extractValues(value);
        date.setValue(values.first == null ? null : values.first.format(DateTimeFormatter.ISO_DATE));
        time.setValue(values.second == null ? null : values.second.format(DateTimeFormatter.ISO_TIME));
    }

    private Pair<LocalDate, LocalTime> extractValues(@Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            return new Pair<>(null, null);
        } else {
            final var dateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
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
    protected void setErrorMessage(@Nullable String message) {
        this.time.setErrorMessage(message);
    }
}
