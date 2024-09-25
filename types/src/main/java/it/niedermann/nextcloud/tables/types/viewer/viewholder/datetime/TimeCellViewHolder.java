package it.niedermann.nextcloud.tables.types.viewer.viewholder.datetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class TimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public TimeCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    protected String formatValue(@Nullable String value) throws DateTimeParseException {
        return LocalTime.parse(value, DateTimeFormatter.ISO_TIME).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
    }

}
