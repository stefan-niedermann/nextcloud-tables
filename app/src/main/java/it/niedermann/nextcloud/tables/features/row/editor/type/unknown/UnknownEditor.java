package it.niedermann.nextcloud.tables.features.row.editor.type.unknown;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.EditTextviewBinding;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;

public class UnknownEditor extends DataEditView<EditTextviewBinding> {

    public UnknownEditor(@NonNull Context context) {
        super(context, EditTextviewBinding.inflate(LayoutInflater.from(context)));
    }

    public UnknownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, EditTextviewBinding.inflate(LayoutInflater.from(context)));
    }

    public UnknownEditor(@NonNull Context context,
                         @Nullable FragmentManager fragmentManager,
                         @NonNull Column column) {
        super(context, EditTextviewBinding.inflate(LayoutInflater.from(context)), column, fragmentManager);
        binding.getRoot().setHint(column.getTitle());
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_question_mark_24);
        binding.getRoot().setEnabled(false);
        binding.getRoot().setHelperText(context.getString(R.string.unsupported_column_type, column.getDataType()));
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        final var src = Optional
                .of(fullData.getData())
                .map(Data::getValue);

        final var value = Stream.of(
                        src.map(Value::getStringValue),
                        src.map(Value::getBooleanValue)
                                .map(String::valueOf),
                        src.map(Value::getTimeValue)
                                .map(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)::format),
                        src.map(Value::getDateValue)
                                .map(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)::format),
                        src.map(Value::getInstantValue)
                                .map(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)::format),
                        src.map(Value::getDoubleValue)
                                .map(String::valueOf)
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));

        binding.editText.setText(value);
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }
}
