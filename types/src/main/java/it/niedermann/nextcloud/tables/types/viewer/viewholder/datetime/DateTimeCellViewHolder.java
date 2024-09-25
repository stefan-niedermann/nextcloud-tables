package it.niedermann.nextcloud.tables.types.viewer.viewholder.datetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class DateTimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateTimeCellViewHolder(@NonNull TableviewCellBinding binding,
                                  @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    protected String formatValue(@Nullable String value) throws DateTimeParseException {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }
}
