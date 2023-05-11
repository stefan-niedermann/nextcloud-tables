package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public abstract class AbstractDateTimeCellViewHolder extends CellViewHolder {

    private static final String TAG = AbstractDateTimeCellViewHolder.class.getSimpleName();
    private final TableviewCellBinding binding;
    private final DateTimeFormatter parseFormatter;
    private final DateTimeFormatter renderFormatter;

    public AbstractDateTimeCellViewHolder(@NonNull TableviewCellBinding binding,
                                          @NonNull DateTimeFormatter parseFormatter,
                                          @NonNull DateTimeFormatter renderFormatter) {
        super(binding.getRoot());

        this.binding = binding;
        this.parseFormatter = parseFormatter;
        this.renderFormatter = renderFormatter;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        try {
            final var value = data == null || TextUtils.isEmpty(String.valueOf(data.getValue()))
                    ? column.getDatetimeDefault()
                    : String.valueOf(data.getValue());
            binding.data.setText(LocalDateTime.parse(value, parseFormatter).format(renderFormatter));
        } catch (DateTimeParseException e) {
            Log.i(TAG, e.getMessage());
            binding.data.setText(column.getDatetimeDefault());
        }

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
