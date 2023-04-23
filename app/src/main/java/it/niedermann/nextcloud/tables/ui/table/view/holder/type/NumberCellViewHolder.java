package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class NumberCellViewHolder extends CellViewHolder {

    private final TableviewCellBinding binding;

    public NumberCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.data.setText(null);
        } else {
            final var subtype = column.getSubtype();

            //noinspection SwitchStatementWithTooFewBranches
            switch (subtype) {
                case "progress": {
                    try {
                        setText(column, String.valueOf(Long.parseLong(String.valueOf(data.getValue()))));
                    } catch (NumberFormatException noLongException) {
                        setText(column, String.valueOf(data.getValue()));
                    }
                    break;
                }
                default: {
                    try {
                        setText(column, String.valueOf(Long.parseLong(String.valueOf(data.getValue()))));
                    } catch (NumberFormatException noLongException) {
                        try {
                            setText(column, String.valueOf(Double.parseDouble(String.valueOf(data.getValue()))));
                        } catch (NumberFormatException noDoubleException) {
                            setText(column, String.valueOf(data.getValue()));
                        }
                    }
                    break;
                }
            }
        }
        binding.data.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();
    }

    @SuppressLint("SetTextI18n")
    private void setText(@NonNull Column column, String number) {
        binding.data.setText(column.getNumberPrefix() + number + column.getNumberSuffix());
    }
}
