package it.niedermann.nextcloud.tables.features.column.edit.types.text;


import static java.util.function.Predicate.not;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;
import java.util.Set;

import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageTextBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;

public class TextManager extends ColumnEditView<ManageTextBinding> {

    public TextManager(@NonNull Context context) {
        super(context);
    }

    public TextManager(@NonNull Context context,
                       @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextManager(@NonNull Context context,
                       @Nullable FragmentManager fragmentManager) {
        super(context, ManageTextBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        fullColumn.getColumn().getDefaultValue().setStringValue(
                Optional.ofNullable(binding.textDefault.getText())
                        .map(Editable::toString)
                        .orElse(null));

        fullColumn.getColumn().setTextAttributes(new TextAttributes(
                Optional.ofNullable(binding.textAllowedPattern.getText())
                        .map(Editable::toString)
                        .orElse(fullColumn.getColumn().getTextAttributes().textAllowedPattern()),

                Optional.ofNullable(binding.textMaxLength.getText())
                        .map(Object::toString)
                        .filter(not(String::isEmpty))
                        .map(Integer::parseInt)
                        .orElse(fullColumn.getColumn().getTextAttributes().textMaxLength())
        ));

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        final var attributes = fullColumn.getColumn().getTextAttributes();

        binding.textAllowedPattern.setText(attributes.textAllowedPattern());
        binding.textMaxLength.setText(
                Optional.ofNullable(attributes.textMaxLength())
                        .map(String::valueOf)
                        .orElse(null));

        binding.textDefault.setText(fullColumn.getColumn().getDefaultValue().getStringValue());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Set.of(
                binding.textDefault,
                binding.textAllowedPattern,
                binding.textMaxLength
        ).forEach(view -> view.setEnabled(enabled));
    }
}
