package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.format.DateTimeParseException;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public abstract class AbstractDateTimeCellViewHolder extends CellViewHolder {

    private static final String TAG = AbstractDateTimeCellViewHolder.class.getSimpleName();
    private final TableviewCellBinding binding;

    public AbstractDateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        try {
            final var value = data == null || TextUtils.isEmpty(data.getValue())
                    ? column.getDatetimeDefault()
                    : data.getValue();
            binding.data.setText(formatValue(value));
        } catch (DateTimeParseException e) {
            Log.i(TAG, e.getMessage());
            binding.data.setText(column.getDatetimeDefault());
        }

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    protected abstract String formatValue(@Nullable String value) throws DateTimeParseException;
}
