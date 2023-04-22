package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

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
    public void bind(@NonNull Data data, @NonNull Column column) {
        binding.data.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        if (TextUtils.isEmpty(String.valueOf(data.getValue())) || DATETIME_NONE.equals(data.getValue())) {
            binding.data.setText("");
            return;
        }

        final var subtype = column.getSubtype();
        switch (subtype) {
            case "": {
                binding.data.setText("");
                break;
            }
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
            default: {
                // TODO
                break;
            }
        }
        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();
    }
}
