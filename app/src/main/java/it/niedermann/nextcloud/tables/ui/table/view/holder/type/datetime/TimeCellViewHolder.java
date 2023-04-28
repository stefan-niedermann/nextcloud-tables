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
        try {
            final LocalTime time;
            if (data == null || TextUtils.isEmpty(String.valueOf(data.getValue())) || DATETIME_NONE.equals(data.getValue())) {
                time = LocalTime.parse(column.getDatetimeDefault(), DateTimeFormatter.ISO_TIME);
            } else {
                time = LocalTime.parse(String.valueOf(data.getValue()), DateTimeFormatter.ISO_TIME);
            }
            binding.data.setText(time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        } catch (Exception e) {
            e.printStackTrace();
            binding.data.setText(column.getDatetimeDefault());
        }
        binding.data.requestLayout();
    }
}
