package it.niedermann.nextcloud.tables.ui.row.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditDatetimeBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class DateTimeEditor extends ColumnEditView {

    protected EditDatetimeBinding binding;
    protected DateEditor date;
    protected TimeEditor time;

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

        date = new DateEditor(context, fragmentManager, column, data);
        time = new TimeEditor(context, fragmentManager, column, data);

        final var dateLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        dateLayoutParams.setMarginEnd(DimensionUtil.INSTANCE.dpToPx(context, R.dimen.spacer_1x));
        final var timeLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        timeLayoutParams.setMarginStart(DimensionUtil.INSTANCE.dpToPx(context, R.dimen.spacer_1x));

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

        if (date == null || time == null) {
            return null;
        }

        final var localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        final var localTime = LocalTime.parse(time, DateTimeFormatter.ISO_TIME);

        return localDate.atTime(localTime).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    protected void setValue(@Nullable String value) {
        if (value == null) {
            date.setValue(null);
            time.setValue(null);
        } else if(value.isBlank()) {
            date.setValue("");
            time.setValue("");
        } else {
            final var dateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);

            date.setValue(dateTime.format(DateTimeFormatter.ISO_DATE));
            time.setValue(dateTime.format(DateTimeFormatter.ISO_TIME));
        }
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {

    }
}
