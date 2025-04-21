package it.niedermann.nextcloud.tables.features.column.edit.types.selection;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.attributes.SelectionAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ItemManageOptionMultiBinding;
import it.niedermann.nextcloud.tables.databinding.ManageSelectionMultiBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.row.editor.OnTextChangedListener;

public class SelectionMultiManager extends ColumnEditView<ManageSelectionMultiBinding> {

    private OptionAdapter adapter;

    public SelectionMultiManager(@NonNull Context context) {
        super(context);
    }

    public SelectionMultiManager(@NonNull Context context,
                                 @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectionMultiManager(@NonNull Context context,
                                 @Nullable FragmentManager fragmentManager) {
        super(context, ManageSelectionMultiBinding.inflate(LayoutInflater.from(context)), fragmentManager);

        adapter = new OptionAdapter(isEnabled());
        binding.options.setAdapter(adapter);
        binding.addOption.setOnClickListener(v -> this.adapter.addSelectionOption(new SelectionOption()));
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {

        // Remove artificial SelectionOption if present
        if (fullColumn.getColumn().getRemoteId() != null ||
            !fullColumn.getSelectionOptions().isEmpty() ||
            adapter.getSelectionOptions().size() != 1 ||
            adapter.getSelectionOptions().get(0).getRemoteId() != null ||
            !TextUtils.isEmpty(adapter.getSelectionOptions().get(0).getLabel())) {

            fullColumn.setDefaultSelectionOptions(adapter.getSelectionDefault());
            fullColumn.setSelectionOptions(adapter.getSelectionOptions());
            fullColumn.getColumn().setSelectionAttributes(new SelectionAttributes());

        }

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        final var createMode = fullColumn.getColumn().getRemoteId() == null
                               && fullColumn.getSelectionOptions().isEmpty();

        // Adds an artificial Selection Option in create mode
        adapter.setSelectionOptions(
                createMode
                        ? List.of(new SelectionOption())
                        : fullColumn.getSelectionOptions(),
                createMode
                        ? Collections.emptyList()
                        : fullColumn.getDefaultSelectionOptions()
        );
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        adapter.setEnabled(enabled);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final var parentState = super.onSaveInstanceState();
        final var args = new Bundle();
        args.putSerializable("selectionDefault", adapter.getSelectionDefault());
        args.putSerializable("selectionOptions", adapter.getSelectionOptions());
        return parentState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        if (state instanceof Bundle bundle) {
            if (bundle.containsKey("selectionOptions")) {
                //noinspection unchecked
                adapter.setSelectionOptions(
                        requireNonNull((ArrayList<SelectionOption>) bundle.getSerializable("selectionOptions")),
                        requireNonNull((ArrayList<SelectionOption>) bundle.getSerializable("selectionDefault"))
                );
            }
        }
    }

    private static class OptionAdapter extends RecyclerView.Adapter<OptionViewHolder> {

        private boolean enabled;

        private OptionAdapter(boolean enabled) {
            this.enabled = enabled;
        }

        private final ArrayList<SelectionOption> selectionOptions = new ArrayList<>();
        private final ArrayList<SelectionOption> defaultOptions = new ArrayList<>();

        @NonNull
        @Override
        public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OptionViewHolder(ItemManageOptionMultiBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
            final var selectionOption = selectionOptions.get(position);
            holder.bind(
                    selectionOptions.get(position),
                    (option, checked) -> {
                        if (checked) {
                            defaultOptions.add(option);
                        } else {
                            defaultOptions.remove(option);
                        }
                    },
                    option -> {
                        // TODO Label changed
                    },
                    option -> {
                        selectionOptions.remove(option);
                        defaultOptions.remove(option);
                        notifyItemRemoved(position);
                    },
                    defaultOptions.contains(selectionOption), // FIXME
                    enabled
            );
        }

        public void setSelectionOptions(
                @NonNull Collection<SelectionOption> selectionOptions,
                @NonNull Collection<SelectionOption> defaultOptions) {
            this.selectionOptions.clear();
            this.selectionOptions.addAll(selectionOptions);
            this.defaultOptions.clear();
            this.defaultOptions.addAll(defaultOptions);
            notifyDataSetChanged();
        }

        @NonNull
        public ArrayList<SelectionOption> getSelectionOptions() {
            return selectionOptions;
        }

        @NonNull
        public ArrayList<SelectionOption> getSelectionDefault() {
            return defaultOptions;
        }

        public void addSelectionOption(@NonNull SelectionOption selectionOption) {
            this.selectionOptions.add(selectionOption);
            notifyItemInserted(this.selectionOptions.size() - 1);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return this.selectionOptions.size();
        }
    }

    private static class OptionViewHolder extends RecyclerView.ViewHolder {

        private final ItemManageOptionMultiBinding binding;

        public OptionViewHolder(@NonNull ItemManageOptionMultiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull SelectionOption selectionOption,
                         @NonNull BiConsumer<SelectionOption, Boolean> onCheckedChange,
                         @NonNull Consumer<SelectionOption> onLabelChanged,
                         @NonNull Consumer<SelectionOption> onDelete,
                         boolean checked,
                         boolean enabled) {
            binding.label.setText(selectionOption.getLabel());
            binding.label.addTextChangedListener((OnTextChangedListener) (content, i, i1, i2) -> {
                selectionOption.setLabel(content.toString());
                onLabelChanged.accept(selectionOption);
            });

            binding.checkbox.setChecked(checked);
            binding.checkbox.setOnCheckedChangeListener((v, newCheckedState) -> onCheckedChange.accept(selectionOption, newCheckedState));

            binding.delete.setOnClickListener(v -> onDelete.accept(selectionOption));

            Stream.of(binding.checkbox, binding.label)
                    .forEach(view -> view.setEnabled(enabled));

            binding.delete.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }
}
