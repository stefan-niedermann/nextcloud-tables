package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class TextCellViewHolder extends CellViewHolder {
    private final TableviewCellBinding binding;

    public TextCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.data.setText(null);
        } else {
            final var subtype = column.getSubtype();

            switch (subtype) {
                case "long": {
                    binding.data.setText(HtmlCompat.fromHtml(String.valueOf(data.getValue()), 0));
                    break;
                }
                case "line":
                default: {
                    binding.data.setText(String.valueOf(data.getValue()));
                    break;
                }
            }
        }
    }
}
