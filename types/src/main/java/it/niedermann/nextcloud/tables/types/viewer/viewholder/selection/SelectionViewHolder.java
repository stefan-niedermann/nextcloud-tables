package it.niedermann.nextcloud.tables.types.viewer.viewholder.selection;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class SelectionViewHolder extends CellViewHolder {
    protected final TableviewCellBinding binding;

    public SelectionViewHolder(@NonNull TableviewCellBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        this.bind(value, column, Collections.emptyList());
    }

    public void bind(@NonNull JsonElement value, @NonNull Column column, @NonNull List<SelectionOption> selectionOptions) {
        binding.data.setText(value.isJsonNull() ? null : formatValue(value, column.getId(), selectionOptions));

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.getRoot().requestLayout();
    }

    protected String formatValue(@NonNull JsonElement value, long columnId, @NonNull List<SelectionOption> selectionOptions) {
        if (!value.isJsonPrimitive()) {
            return "";
        }

        return getLabel(value.getAsLong(), columnId, selectionOptions)
                .orElse("");
    }

    @NonNull
    protected Optional<String> getLabel(long remoteSelectionOptionId, long columnId, @NonNull List<SelectionOption> selectionOptions) {
        for (final var selectionOption : selectionOptions) {
            if (Objects.equals(columnId, selectionOption.getColumnId()) &&
                    Objects.equals(remoteSelectionOptionId, selectionOption.getRemoteId())) {
                return Optional.of(selectionOption.getLabel());
            }
        }

        return Optional.empty();
    }
}
