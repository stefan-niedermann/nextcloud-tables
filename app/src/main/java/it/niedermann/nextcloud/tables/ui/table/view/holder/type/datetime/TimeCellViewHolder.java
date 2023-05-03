package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class TimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public TimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    protected DateTimeFormatter getParseFormatter() {
        return DateTimeFormatter.ISO_TIME;
    }

    @Override
    protected DateTimeFormatter getRenderFormatter() {
        return DateTimeFormatter.ofLocalizedTime(getRenderFormatStyle());
    }
}
