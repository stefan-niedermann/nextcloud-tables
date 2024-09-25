package it.niedermann.nextcloud.tables.types.viewer.viewholder.datetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class DateCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    protected String formatValue(@Nullable String value) throws DateTimeParseException {
        return LocalDate.parse(value, DateTimeFormatter.ISO_DATE).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }
}
