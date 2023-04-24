package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class DateTimeCellViewHolder extends CellViewHolder {
    private static final String DATETIME_NONE = "none";
    private final TableviewCellBinding binding;

    public DateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null || TextUtils.isEmpty(String.valueOf(data.getValue())) || DATETIME_NONE.equals(data.getValue())) {
            binding.data.setText("");
        } else {
            final var subtype = column.getSubtype();
            switch (subtype) {
                case "datetime": {
                    final var date = LocalDate.parse(String.valueOf(data.getValue()), DateTimeFormatter.ISO_DATE_TIME);
                    binding.data.setText(date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                    break;
                }
                case "date": {
                    final var date = LocalDate.parse(String.valueOf(data.getValue()), DateTimeFormatter.ISO_DATE);
                    binding.data.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    break;
                }
                case "time": {
                    final var date = LocalTime.parse(String.valueOf(data.getValue()), DateTimeFormatter.ISO_TIME);
                    binding.data.setText(date.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
                    break;
                }
                case "":
                default: {
                    // TODO
                    binding.data.setText("");
                    break;
                }
            }
        }
        binding.data.requestLayout();
    }
}
