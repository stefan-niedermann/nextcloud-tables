package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class DateTimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    protected String formatValue(@Nullable String value) throws DateTimeParseException {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }
}
