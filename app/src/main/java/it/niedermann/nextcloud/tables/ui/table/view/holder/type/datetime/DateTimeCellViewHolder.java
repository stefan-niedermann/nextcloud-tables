package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class DateTimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    private static final DateTimeFormatter TABLES_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .toFormatter();

    public DateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding, TABLES_LOCAL_DATE_TIME, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }
}
