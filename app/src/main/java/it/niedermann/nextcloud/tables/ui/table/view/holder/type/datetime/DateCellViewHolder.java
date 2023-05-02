package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class DateCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    protected DateTimeFormatter getParseFormatter() {
        return DateTimeFormatter.ISO_DATE;
    }

    @Override
    protected DateTimeFormatter getRenderFormatter() {
        return DateTimeFormatter.ofLocalizedDate(DEFAULT_RENDER_FORMAT_STYLE);
    }
}
