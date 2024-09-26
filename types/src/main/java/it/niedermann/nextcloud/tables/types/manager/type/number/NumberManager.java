package it.niedermann.nextcloud.tables.types.manager.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.ManageNumberBinding;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;

public class NumberManager extends ColumnManageView {

    protected ManageNumberBinding binding;

    public NumberManager(@NonNull Context context) {
        super(context);
    }

    public NumberManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberManager(@NonNull Context context, @NonNull Column column, @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = ManageNumberBinding.inflate(LayoutInflater.from(context));
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Column getColumn() {
        column.setNumberDefault(Double.parseDouble(Objects.requireNonNull(binding.numberDefault.getText()).toString()));
        column.setNumberMin(Double.parseDouble(Objects.requireNonNull(binding.numberMin.getText()).toString()));
        column.setNumberMax(Double.parseDouble(Objects.requireNonNull(binding.numberMax.getText()).toString()));
        column.setNumberDecimals(Integer.parseInt(Objects.requireNonNull(binding.numberDecimals.getText()).toString()));
        column.setNumberPrefix(Objects.requireNonNullElse(binding.numberPrefix.getText(), "").toString());
        column.setNumberSuffix(Objects.requireNonNullElse(binding.numberSuffix.getText(), "").toString());
        return super.getColumn();
    }

    @Override
    protected void setColumn(@NonNull Column column) {
        super.setColumn(column);
        // FIXME String.valueOf(null) writes null into the inputs
        binding.numberDefault.setText(String.valueOf(column.getNumberDefault()));
        binding.numberMin.setText(String.valueOf(column.getNumberMin()));
        binding.numberMax.setText(String.valueOf(column.getNumberMax()));
        binding.numberDecimals.setText(String.valueOf(column.getNumberDecimals()));
        binding.numberPrefix.setText(column.getNumberPrefix());
        binding.numberSuffix.setText(column.getNumberSuffix());
    }
}
