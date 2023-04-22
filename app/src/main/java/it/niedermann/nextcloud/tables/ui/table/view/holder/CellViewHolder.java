package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellLayoutBinding;

public class CellViewHolder extends AbstractViewHolder {
    private static final String DATETIME_NONE = "none";
    private static final String SELECTION_CHECK_TRUE = "true";
    private static final String SELECTION_CHECK_FALSE = "false";
    private final TableviewCellLayoutBinding binding;

    public CellViewHolder(@NonNull TableviewCellLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Data cellModel, @NonNull Column column) {
        final var type = column.getType();
        try {
            switch (type) {
                case "text": {
                    bindText(cellModel);
                    break;
                }
                case "datetime": {
                    bindDate(cellModel, column);
                    break;
                }
                case "selection": {
                    bindSelection(cellModel, column);
                    break;
                }
                default: {
                    bindUnknown(cellModel);
                    break;
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                throw e;
            } else {
                e.printStackTrace();
            }
            bindUnknown(cellModel);
        }
        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();
    }

    private void bindUnknown(@NonNull Data cellModel) {
        binding.data.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.data.setText(String.valueOf(cellModel.getValue()));
    }

    private void bindText(@NonNull Data cellModel) {
        binding.data.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.data.setText(String.valueOf(cellModel.getValue()));
    }

    private void bindDate(@NonNull Data cellModel, @NonNull Column column) {
        binding.data.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        if (TextUtils.isEmpty(String.valueOf(cellModel.getValue())) || DATETIME_NONE.equals(cellModel.getValue())) {
            binding.data.setText("");
            return;
        }

        final var subtype = column.getSubtype();
        switch (subtype) {
            case "datetime": {
                final var date = LocalDate.parse(String.valueOf(cellModel.getValue()), DateTimeFormatter.ISO_DATE_TIME);
                binding.data.setText(date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                break;
            }
            case "date": {
                final var date = LocalDate.parse(String.valueOf(cellModel.getValue()), DateTimeFormatter.ISO_DATE);
                binding.data.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                break;
            }
            case "time": {
                final var date = LocalTime.parse(String.valueOf(cellModel.getValue()), DateTimeFormatter.ISO_TIME);
                binding.data.setText(date.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
                break;
            }
            default: {
                bindUnknown(cellModel);
                break;
            }
        }
    }

    private void bindSelection(@NonNull Data cellModel, @NonNull Column column) {
        binding.data.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        final var subtype = column.getSubtype();
        //noinspection SwitchStatementWithTooFewBranches
        switch (subtype) {
            case "check": {
                if (SELECTION_CHECK_TRUE.equals(cellModel.getValue())) {
                    binding.data.setText("☒");
                } else if (SELECTION_CHECK_FALSE.equals(cellModel.getValue())) {
                    binding.data.setText("☐");
                } else {
                    binding.data.setText("");
                }
                break;
            }
            default: {
                bindUnknown(cellModel);
                break;
            }
        }
    }
}
