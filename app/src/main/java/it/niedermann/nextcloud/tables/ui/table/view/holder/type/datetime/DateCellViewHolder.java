package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class DateCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null || TextUtils.isEmpty(String.valueOf(data.getValue())) || DATETIME_NONE.equals(data.getValue())) {
            final var date = LocalDate.parse(String.valueOf(column.getDatetimeDefault()), DateTimeFormatter.ISO_DATE);
            binding.data.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        } else {
            final var date = LocalDate.parse(String.valueOf(data.getValue()), DateTimeFormatter.ISO_DATE);
            binding.data.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        }
        binding.data.requestLayout();
    }
}
