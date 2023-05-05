package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class TimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public TimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding, DateTimeFormatter.ISO_TIME, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
    }

}
