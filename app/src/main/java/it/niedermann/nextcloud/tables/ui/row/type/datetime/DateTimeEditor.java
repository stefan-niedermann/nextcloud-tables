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

import java.time.Instant;
import java.time.ZoneId;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
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
                          @Nullable Object value) {
        super(context, fragmentManager, column, value);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @Nullable Object value) {
        binding = EditDatetimeBinding.inflate(LayoutInflater.from(context));

        date = new DateEditor(context, fragmentManager, column, value);
        time = new TimeEditor(context, fragmentManager, column, value);

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

        binding.getRoot().addView(date);
        binding.getRoot().addView(time);

        return binding.getRoot();
    }

    @Nullable
    @Override
    protected Instant getValue() {
        final var date = this.date.getValue();
        final var time = this.time.getValue();

        if (date == null || time == null) {
            return null;
        }

        final var localDate = date.atZone(ZoneId.systemDefault()).toLocalDate();
        final var localTime = time.atZone(ZoneId.systemDefault()).toLocalTime();

        return Instant.from(localDate.atTime(localTime));
    }

    @Override
    protected void setValue(@Nullable Object value) {

    }

    @Override
    protected void setErrorMessage(@Nullable String message) {

    }
}
