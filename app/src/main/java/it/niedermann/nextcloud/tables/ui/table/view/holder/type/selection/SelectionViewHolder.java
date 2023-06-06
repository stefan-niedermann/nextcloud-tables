package it.niedermann.nextcloud.tables.ui.table.view.holder.type.selection;

import android.text.TextUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class SelectionViewHolder extends CellViewHolder {
    protected final TableviewCellBinding binding;

    public SelectionViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        this.bind(data, column, Collections.emptyList());
    }

    public void bind(@Nullable Data data, @NonNull Column column, @NonNull List<SelectionOption> selectionOptions) {
        binding.data.setText(data == null ? null : formatValue(data.getValue(), selectionOptions));

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.getRoot().requestLayout();
    }

    protected String formatValue(@Nullable String value, @NonNull List<SelectionOption> selectionOptions) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }

        return Arrays.stream(value.split(","))
                .map(Long::parseLong)
                .sorted()
                .map(id -> getLabel(id, selectionOptions))
                .collect(Collectors.joining(", "));
    }

    private String getLabel(long id, @NonNull List<SelectionOption> selectionOptions) {
        for (final var selectionOption : selectionOptions) {
            if (Objects.equals(id, selectionOption.getRemoteId())) {
                return selectionOption.getLabel();
            }
        }

        return "";
    }
}
