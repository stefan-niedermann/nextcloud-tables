package it.niedermann.nextcloud.tables.types.manager.type.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.ManageTextBinding;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;

public class TextManager extends ColumnManageView {

    protected ManageTextBinding binding;

    public TextManager(@NonNull Context context) {
        super(context);
    }

    public TextManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextManager(@NonNull Context context, @NonNull Column column, @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = ManageTextBinding.inflate(LayoutInflater.from(context));
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Column getColumn() {
        column.setTextDefault(Objects.requireNonNullElse(binding.textDefault.getText(), "").toString());
        column.setTextAllowedPattern(Objects.requireNonNullElse(binding.textAllowedPattern.getText(), "").toString());
        column.setTextMaxLength(Integer.parseInt(Objects.requireNonNull(binding.textMaxLength.getText()).toString()));
        return super.getColumn();
    }

    @Override
    protected void setColumn(@NonNull Column column) {
        super.setColumn(column);
        binding.textDefault.setText(column.getTextDefault());
        binding.textAllowedPattern.setText(column.getTextAllowedPattern());
        binding.textMaxLength.setText(Objects.requireNonNullElse(column.getTextMaxLength(), 0));
    }
}
