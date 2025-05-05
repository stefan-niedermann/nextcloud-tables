package it.niedermann.nextcloud.tables.features.column.edit;

import static java.util.Collections.emptySet;
import static java.util.concurrent.CompletableFuture.completedFuture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.databinding.ItemOptionBinding;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;
import it.niedermann.nextcloud.tables.ui.twolevelselect.TwoLevelSelect;

///
public class EDataTypePicker extends TwoLevelSelect<EDataType.EDataTypeGroup, EDataType, EDataType> {

    public EDataTypePicker(@NonNull Context context) {
        this(context, null);
    }

    public EDataTypePicker(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs,
                new TypeAdapter(context, R.layout.item_option),
                new SubTypeAdapter(context, R.layout.item_option));

        final var firstLevelContent = Arrays.stream(EDataType.EDataTypeGroup.values())
                .filter(EDataType.EDataTypeGroup::supportsEditing)
                .sorted()
                .toList();

        setFirstLevelContent(firstLevelContent);
    }

    @NonNull
    @Override
    protected CompletableFuture<Collection<EDataType>> getSecondLevelContent(@Nullable EDataType.EDataTypeGroup firstLevel) {
        if (firstLevel == null) {
            return completedFuture(emptySet());
        }

        final var secondLevelContent = firstLevel
                .getDataTypes()
                .stream()
                .filter(EDataType::supportsEditing)
                .sorted()
                .toList();

        return completedFuture(secondLevelContent);
    }

    @NonNull
    @Override
    protected Optional<EDataType> getResult(@NonNull EDataType.EDataTypeGroup firstLevel,
                                            @Nullable EDataType secondLevel) {
        return Optional.ofNullable(secondLevel);
    }

    @Override
    protected int getFirstLevelHint() {
        return R.string.simple_type;
    }

    @Override
    protected int getSecondLevelHint() {
        return R.string.simple_subtype;
    }

    @NonNull
    @Override
    protected Optional<String> getFirstLevelLabel(@Nullable EDataType.EDataTypeGroup firstLevel) {
        return Optional
                .ofNullable(firstLevel)
                .map(group -> group.humanReadableValue)
                .map(getContext()::getString);
    }

    @NonNull
    @Override
    protected Optional<String> getSecondLevelLabel(@Nullable EDataType secondLevel) {
        return Optional
                .ofNullable(secondLevel)
                .flatMap(EDataType::getHumanReadableSubTypeStringRes)
                .map(getContext()::getString);
    }

    static class TypeAdapter extends ArrayAdapter<EDataType.EDataTypeGroup> {

        public TypeAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView,
                                    @NonNull ViewGroup parent) {
            return bind(getItem(position), position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return bind(getItem(position), position, convertView, parent);
        }

        @NonNull
        private View bind(@Nullable EDataType.EDataTypeGroup group,
                          int position,
                          @Nullable View convertView,
                          @NonNull ViewGroup parent) {

            if (group == null) {
                if (FeatureToggle.STRICT_MODE.enabled) {
                    throw new IllegalStateException("Can not find " + EDataType.EDataTypeGroup.class.getSimpleName() + " at position " + position);
                }

                return super.getView(position, convertView, parent);
            }

            final var binding = convertView == null
                    ? ItemOptionBinding.inflate(LayoutInflater.from(getContext()), parent, false)
                    : ItemOptionBinding.bind(convertView);

            binding.getRoot().setText(group.humanReadableValue);

            return binding.getRoot();
        }
    }

    static class SubTypeAdapter extends ArrayAdapter<EDataType> {

        public SubTypeAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView,
                                    @NonNull ViewGroup parent) {
            return bind(getItem(position), position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return bind(getItem(position), position, convertView, parent);
        }

        @NonNull
        private View bind(@Nullable EDataType dataType,
                          int position,
                          @Nullable View convertView,
                          @NonNull ViewGroup parent) {

            if (dataType == null) {
                if (FeatureToggle.STRICT_MODE.enabled) {
                    throw new IllegalStateException("Can not find " + EDataType.class.getSimpleName() + " at position " + position);
                }
                return super.getView(position, convertView, parent);

            }

            final var binding = convertView == null
                    ? ItemOptionBinding.inflate(LayoutInflater.from(getContext()), parent, false)
                    : ItemOptionBinding.bind(convertView);

            dataType.getHumanReadableSubTypeStringRes()
                    .ifPresentOrElse(
                            binding.getRoot()::setText,
                            () -> binding.getRoot().setText(null));

            return binding.getRoot();
        }
    }
}
