package it.niedermann.nextcloud.tables.features.row.editor.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.EditDatetimeBinding;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;

public class DateTimeEditor extends DataEditView<EditDatetimeBinding> {

    protected DateTimeDateEditor date;
    protected DateTimeTimeEditor time;

    public DateTimeEditor(@NonNull Context context) {
        super(context, EditDatetimeBinding.inflate(LayoutInflater.from(context)));
    }

    public DateTimeEditor(@NonNull Context context,
                          @Nullable AttributeSet attrs) {
        super(context, attrs, EditDatetimeBinding.inflate(LayoutInflater.from(context)));
    }

    public DateTimeEditor(@NonNull Context context,
                          @Nullable FragmentManager fragmentManager,
                          @NonNull Column column) {
        super(context, EditDatetimeBinding.inflate(LayoutInflater.from(context)), column, fragmentManager);

        if (fragmentManager == null) {
            throw new IllegalArgumentException(FragmentManager.class.getSimpleName() + " is required for a " + DateTimeEditor.class.getSimpleName());
        }

        try {
            date = new DateTimeDateEditor(context, fragmentManager, column);
            time = new DateTimeTimeEditor(context, fragmentManager, column);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final var horizontalMargin = context.getResources().getDimensionPixelSize(it.niedermann.nextcloud.tables.ui.R.dimen.spacer_1x);

        final var dateLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1);
        dateLayoutParams.setMarginEnd(horizontalMargin);

        final var timeLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1);
        timeLayoutParams.setMarginStart(horizontalMargin);

        date.setLayoutParams(dateLayoutParams);
        time.setLayoutParams(timeLayoutParams);

        binding.title.setText(column.getTitle());
        binding.dateAndTimePickers.addView(date);
        binding.dateAndTimePickers.addView(time);
    }

    @Override
    @Nullable
    public FullData getFullData() {
        final var date = Optional
                .ofNullable(this.date.getFullData())
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getDateValue);

        final var time = Optional
                .ofNullable(this.time.getFullData())
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getTimeValue);

        final var value = Optional
                .ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue);

        if (date.isEmpty()) {
            value.ifPresent(val -> val.setInstantValue(null));

        } else if (time.isEmpty()) {
            value.ifPresent(val -> val.setInstantValue(date
                    .map(d -> d.atTime(0, 0))
                    .map(Instant::from)
                    .orElse(null)));

        } else {
            value.ifPresent(val -> val.setInstantValue(date
                    .map(d -> d.atTime(time.get().getHour(), time.get().getMinute()))
                    .map(Instant::from)
                    .orElse(null)));
        }

        return fullData;
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getInstantValue)
                .map(LocalDateTime::from);

        final var dateData = new FullData(fullData);
        Optional.of(dateData.getData())
                .map(Data::getValue)
                .ifPresent(val -> {
                    val.setInstantValue(null);
                    val.setDateValue(value
                            .map(LocalDate::from)
                            .orElse(null));
                });
        date.setFullData(dateData);

        final var timeData = new FullData(fullData);
        Optional.of(timeData.getData())
                .map(Data::getValue)
                .ifPresent(val -> {
                    val.setInstantValue(null);
                    val.setTimeValue(value
                            .map(LocalTime::from)
                            .orElse(null));
                });
        time.setFullData(timeData);
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        final var date = Optional
                .ofNullable(this.date.getFullData())
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getDateValue);

        final var time = Optional
                .ofNullable(this.time.getFullData())
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getTimeValue);

        if (time.isEmpty()) {
            return Optional.of(getContext().getString(R.string.validation_time_missing));

        } else if (column.isMandatory() && date.isEmpty()) {
            return Optional.of(getContext().getString(R.string.validation_mandatory));

        }

        return Optional.empty();
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        this.time.setErrorMessage(message);
    }
}
