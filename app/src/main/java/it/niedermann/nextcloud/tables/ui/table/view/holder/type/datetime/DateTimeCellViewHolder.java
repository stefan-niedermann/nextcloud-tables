package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class DateTimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    protected DateTimeFormatter getParseFormatter() {
        return DateTimeFormatter.ISO_DATE_TIME;
    }

    @Override
    protected DateTimeFormatter getRenderFormatter() {
        return DateTimeFormatter.ofLocalizedDateTime(DEFAULT_RENDER_FORMAT_STYLE);
    }
}
