package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class DateTimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        try {
            final LocalDateTime dateTime;
            if (data == null || TextUtils.isEmpty(String.valueOf(data.getValue())) || DATETIME_NONE.equals(data.getValue())) {
                dateTime = LocalDateTime.parse(column.getDatetimeDefault(), DateTimeFormatter.ISO_DATE_TIME);
            } else {
                dateTime = LocalDateTime.parse(String.valueOf(data.getValue()), DateTimeFormatter.ISO_DATE_TIME);
            }
            binding.data.setText(dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        } catch (Exception e) {
            e.printStackTrace();
            binding.data.setText(column.getDatetimeDefault());
        }
        binding.data.requestLayout();
    }
}
