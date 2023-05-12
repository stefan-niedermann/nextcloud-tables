package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class TimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public TimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    protected String formatValue(@Nullable String value) throws DateTimeParseException {
        return LocalTime.parse(value, DateTimeFormatter.ISO_TIME).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
    }

}
