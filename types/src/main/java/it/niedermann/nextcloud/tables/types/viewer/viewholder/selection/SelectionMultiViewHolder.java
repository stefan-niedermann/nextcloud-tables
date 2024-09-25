package it.niedermann.nextcloud.tables.types.viewer.viewholder.selection;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class SelectionMultiViewHolder extends SelectionViewHolder {

    public SelectionMultiViewHolder(@NonNull TableviewCellBinding binding,
                                    @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    protected String formatValue(@NonNull JsonElement value, long columnId, @NonNull List<SelectionOption> selectionOptions) {
        if (!value.isJsonArray()) {
            return "";
        }

        return value.getAsJsonArray().asList().stream()
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsLong)
                .sorted()
                .map(remoteSelectionOptionId -> getLabel(remoteSelectionOptionId, columnId, selectionOptions))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));
    }
}
