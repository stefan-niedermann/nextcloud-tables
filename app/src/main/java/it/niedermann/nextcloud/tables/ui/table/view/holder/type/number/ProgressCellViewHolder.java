package it.niedermann.nextcloud.tables.ui.table.view.holder.type.number;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.TablesApplication.FeatureToggle;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class ProgressCellViewHolder extends CellViewHolder {

    private final TableviewCellProgressBinding binding;

    public ProgressCellViewHolder(@NonNull TableviewCellProgressBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.progress.setProgressCompat(0, false);
        } else {
            final var value = data.getValue();
            try {
                final var progress = TextUtils.isEmpty(value) ? 0 : Integer.parseInt(data.getValue());
                binding.progress.setProgressCompat(progress, false);
            } catch (NumberFormatException e) {
                binding.progress.setProgressCompat(0, false);
                e.printStackTrace();

                if (FeatureToggle.STRICT_MODE.enabled) {
                    throw new IllegalArgumentException("Could not parse progress: " + value, e);
                }
            }
        }
    }
}
