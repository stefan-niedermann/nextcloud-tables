package it.niedermann.nextcloud.tables.types.viewer.viewholder.number;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.BuildConfig;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class ProgressCellViewHolder extends CellViewHolder {

    private final TableviewCellProgressBinding binding;

    public ProgressCellViewHolder(@NonNull TableviewCellProgressBinding binding,
                                  @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        try {
            final var progress = value.getAsInt();
            binding.progress.setProgressCompat(progress, false);
        } catch (NumberFormatException e) {
            binding.progress.setProgressCompat(0, false);
            e.printStackTrace();

            if (BuildConfig.DEBUG) {
                throw new IllegalArgumentException("Could not parse progress: " + value, e);
            }
        }
    }
}
