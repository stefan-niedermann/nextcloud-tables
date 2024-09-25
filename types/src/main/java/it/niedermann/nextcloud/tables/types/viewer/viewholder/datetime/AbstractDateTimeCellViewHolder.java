package it.niedermann.nextcloud.tables.types.viewer.viewholder.datetime;

import android.text.TextUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;

import java.time.format.DateTimeParseException;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.BuildConfig;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public abstract class AbstractDateTimeCellViewHolder extends CellViewHolder {

    private static final String TAG = AbstractDateTimeCellViewHolder.class.getSimpleName();
    private final TableviewCellBinding binding;

    public AbstractDateTimeCellViewHolder(@NonNull TableviewCellBinding binding,
                                          @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        final var jsonValue = value.getAsString();
        try {
            binding.data.setText(TextUtils.isEmpty(jsonValue) ? null : formatValue(jsonValue));
        } catch (DateTimeParseException e) {
            binding.data.setText(null);
            e.printStackTrace();

            if (BuildConfig.DEBUG) {
                throw new IllegalArgumentException("Could not parse number " + jsonValue, e);
            }
        }

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    protected abstract String formatValue(@Nullable String value) throws DateTimeParseException;
}
