package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class TimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public TimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null || TextUtils.isEmpty(String.valueOf(data.getValue())) || DATETIME_NONE.equals(data.getValue())) {
            final var time = LocalTime.parse(String.valueOf(column.getDatetimeDefault()), DateTimeFormatter.ISO_TIME);
            binding.data.setText(time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        } else {
            final var time = LocalTime.parse(String.valueOf(data.getValue()), DateTimeFormatter.ISO_TIME);
            binding.data.setText(time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        }
        binding.data.requestLayout();
    }
}
