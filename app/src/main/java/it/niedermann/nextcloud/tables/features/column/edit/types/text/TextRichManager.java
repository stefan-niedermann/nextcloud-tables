package it.niedermann.nextcloud.tables.features.column.edit.types.text;


import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageTextRichBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;

public class TextRichManager extends ColumnEditView<ManageTextRichBinding> {

    public TextRichManager(@NonNull Context context) {
        super(context);
    }

    public TextRichManager(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextRichManager(@NonNull Context context,
                           @Nullable FragmentManager fragmentManager) {
        super(context, ManageTextRichBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        fullColumn.getColumn().getDefaultValue().setStringValue(
                Optional.ofNullable(binding.textDefault.getText())
                        .map(Editable::toString)
                        .orElse(null));

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);
        binding.textDefault.setText(fullColumn.getColumn().getDefaultValue().getStringValue());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Stream.of(
                binding.textDefault
        ).forEach(view -> view.setEnabled(enabled));
    }
}
