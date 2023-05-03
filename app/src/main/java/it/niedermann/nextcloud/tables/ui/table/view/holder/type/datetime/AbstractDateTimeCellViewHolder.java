package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public abstract class AbstractDateTimeCellViewHolder extends CellViewHolder {

    private static final String TAG = AbstractDateTimeCellViewHolder.class.getSimpleName();
    public static final String DATETIME_NONE = "none";
    private final TableviewCellBinding binding;

    public AbstractDateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        try {
            final var value = data == null || TextUtils.isEmpty(String.valueOf(data.getValue())) || DATETIME_NONE.equals(data.getValue())
                    ? column.getDatetimeDefault()
                    : String.valueOf(data.getValue());
            binding.data.setText(LocalDateTime.parse(value, getParseFormatter()).format(getRenderFormatter()));
        } catch (DateTimeParseException e) {
            Log.i(TAG, e.getMessage());
            binding.data.setText(column.getDatetimeDefault());
        }
        binding.data.requestLayout();
    }

    protected abstract DateTimeFormatter getParseFormatter();

    protected abstract DateTimeFormatter getRenderFormatter();

    protected FormatStyle getRenderFormatStyle() {
        return FormatStyle.MEDIUM;
    }
}
