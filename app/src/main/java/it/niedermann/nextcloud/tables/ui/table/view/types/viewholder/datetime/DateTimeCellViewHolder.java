package it.niedermann.nextcloud.tables.ui.table.view.types.viewholder.datetime;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class DateTimeCellViewHolder extends AbstractDateTimeCellViewHolder {

    public DateTimeCellViewHolder(@NonNull TableviewCellBinding binding,
                                  @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    protected String formatValue(@NonNull Data data) throws DateTimeParseException {
        return Optional.ofNullable(data.getValue())
                .map(Value::getInstantValue)
                .map(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)::format)
                .orElse(null);
    }
}
