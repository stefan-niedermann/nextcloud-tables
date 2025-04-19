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
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageTextLineBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;

public class TextLineManager extends ColumnEditView<ManageTextLineBinding> {

    public TextLineManager(@NonNull Context context) {
        super(context);
    }

    public TextLineManager(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLineManager(@NonNull Context context,
                           @Nullable FragmentManager fragmentManager) {
        super(context, ManageTextLineBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        fullColumn.getColumn().getDefaultValue().setStringValue(
                Optional.ofNullable(binding.textDefault.getText())
                        .map(Editable::toString)
                        .orElse(null));

        fullColumn.getColumn().setTextAttributes(new TextAttributes(
                null,
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

        binding.textMaxLength.setText(
                Optional.ofNullable(attributes.textMaxLength())
                        .map(String::valueOf)
                        .orElse(null));

        binding.textDefault.setText(fullColumn.getColumn().getDefaultValue().getStringValue());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Stream.of(
                binding.textDefault,
                binding.textMaxLength
        ).forEach(view -> view.setEnabled(enabled));
    }
}
