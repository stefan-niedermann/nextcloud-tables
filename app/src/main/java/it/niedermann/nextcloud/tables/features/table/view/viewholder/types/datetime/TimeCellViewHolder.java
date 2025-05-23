package it.niedermann.nextcloud.tables.features.table.view.viewholder.types.datetime;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class TimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public TimeCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    protected String formatValue(@NonNull Data data) {
        return Optional.of(data.getValue())
                .map(Value::getTimeValue)
                .map(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)::format)
                .orElse(null);
    }

}
