package it.niedermann.nextcloud.tables.features.column.edit.types.selection;

import static java.util.Collections.max;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;

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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.AbstractEntity;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.attributes.SelectionAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ItemManageOptionSingleBinding;
import it.niedermann.nextcloud.tables.databinding.ManageSelectionSingleBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.row.editor.OnTextChangedListener;

public class SelectionSingleManager extends ColumnEditView<ManageSelectionSingleBinding> {

    private OptionAdapter adapter;

    public SelectionSingleManager(@NonNull Context context) {
        super(context);
    }

    public SelectionSingleManager(@NonNull Context context,
                                  @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectionSingleManager(@NonNull Context context,
                                  @Nullable FragmentManager fragmentManager) {
        super(context, ManageSelectionSingleBinding.inflate(LayoutInflater.from(context)), fragmentManager);

        adapter = new OptionAdapter(isEnabled(), defaultOptionPosition -> binding.clear.setVisibility(defaultOptionPosition == null ? View.INVISIBLE : View.VISIBLE));
        binding.options.setAdapter(adapter);
        binding.addOption.setOnClickListener(v -> this.adapter.addSelectionOption(new SelectionOption()));
        binding.clear.setOnClickListener(v -> {
            adapter.setDefaultSelectionOption(null);
            binding.clear.setVisibility(View.INVISIBLE);
        });
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {

        // Artificial SelectionOption can be set during #setFullColumn
        final var onlyUntouchedArtificialSelectionOption =
                adapter.getSelectionOptions().size() == 1 &&
                adapter.getSelectionOptions().get(0).getRemoteId() == null &&
                TextUtils.isEmpty(adapter.getSelectionOptions().get(0).getLabel());

        if (isCreateMode() || !onlyUntouchedArtificialSelectionOption) {

            final var usedRemoteIds = new HashSet<Long>();

            usedRemoteIds.addAll(fullColumn
                    .getSelectionOptions()
                    .stream()
                    .map(SelectionOption::getRemoteId)
                    .filter(Objects::nonNull)
                    .collect(toUnmodifiableSet()));

            usedRemoteIds.addAll(adapter.getSelectionOptions()
                    .stream()
                    .map(SelectionOption::getRemoteId)
                    .filter(Objects::nonNull)
                    .collect(toUnmodifiableSet()));

            final var maxRemoteId = new AtomicLong(usedRemoteIds.isEmpty() ? -1L : max(usedRemoteIds));

            fullColumn.setDefaultSelectionOptions(adapter.getSelectionDefault()
                    .map(List::of)
                    .orElseGet(Collections::emptyList));
            fullColumn.setSelectionOptions(adapter.getSelectionOptions());
            fullColumn.getSelectionOptions()
                    .stream()
                    .filter(selectionOption -> selectionOption.getRemoteId() == null)
                    .forEach(selectionOption -> selectionOption.setRemoteId(maxRemoteId.incrementAndGet()));
            fullColumn.getColumn().setSelectionAttributes(new SelectionAttributes());
        }

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        final boolean createMode = isCreateMode();

        // Adds an artificial Selection Option in create mode

        adapter.setSelectionOptions(
                createMode
                        ? List.of(new SelectionOption())
                        : fullColumn.getSelectionOptions(),
                createMode
                        ? null
                        : Optional.of(fullColumn.getDefaultSelectionOptions())
                        .filter(not(Collection::isEmpty))
                        .flatMap(list -> list.stream().findAny())
                        .flatMap(item -> findPositionById(fullColumn.getSelectionOptions(), item))
                        .orElse(null)
        );
    }

    @NonNull
    private Optional<Integer> findPositionById(@NonNull List<? extends AbstractEntity> haystack,
                                               @NonNull AbstractEntity needle) {

        for (int position = 0; position < haystack.size(); position++) {
            if (Objects.equals(needle.getId(), haystack.get(position).getId())) {
                return Optional.of(position);
            }
        }

        return Optional.empty();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        adapter.setEnabled(enabled);
        binding.clear.setEnabled(enabled);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final var parentState = super.onSaveInstanceState();
        final var args = new Bundle();
        Optional.ofNullable(adapter.getSelectionDefaultPosition()).ifPresent(def -> args.putInt("selectionDefault", def));
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
                        bundle.getInt("selectionDefault")
                );
            }
        }
    }

    private static class OptionAdapter extends RecyclerView.Adapter<OptionViewHolder> {

        private boolean enabled;
        private final Consumer<Integer> onDefaultOptionPositionChanged;

        private OptionAdapter(boolean enabled,
                              @NonNull Consumer<Integer> onDefaultOptionPositionChanged) {
            this.enabled = enabled;
            this.onDefaultOptionPositionChanged = onDefaultOptionPositionChanged;
        }

        private final ArrayList<SelectionOption> selectionOptions = new ArrayList<>();
        @Nullable
        private Integer defaultOptionPosition = null;

        @NonNull
        @Override
        public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OptionViewHolder(ItemManageOptionSingleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
            final var selectionOption = selectionOptions.get(position);
            holder.bind(
                    position,
                    selectionOption,
                    this::setDefaultSelectionOption,
                    option -> {
                        // TODO Implement warning that associated selections in rows will get deleted
                        selectionOptions.remove(position);
                        defaultOptionPosition = null;
                        notifyItemRemoved(position);
                    },
                    defaultOptionPosition != null && defaultOptionPosition == position,
                    enabled
            );
        }

        public void setSelectionOptions(
                @NonNull List<SelectionOption> selectionOptions,
                @Nullable Integer defaultOptionPosition) {
            this.selectionOptions.clear();
            this.selectionOptions.addAll(selectionOptions);
            setDefaultSelectionOption(defaultOptionPosition);
        }

        public void setDefaultSelectionOption(@Nullable Integer defaultOptionPosition) {
            this.defaultOptionPosition = defaultOptionPosition;
            notifyDataSetChanged();
            onDefaultOptionPositionChanged.accept(this.defaultOptionPosition);
        }

        @NonNull
        public ArrayList<SelectionOption> getSelectionOptions() {
            return selectionOptions;
        }

        @NonNull
        public Optional<SelectionOption> getSelectionDefault() {
            if (defaultOptionPosition == null) {
                return Optional.empty();
            }

            return Optional.of(selectionOptions.get(defaultOptionPosition));
        }

        @Nullable
        public Integer getSelectionDefaultPosition() {
            return defaultOptionPosition;
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

        private final ItemManageOptionSingleBinding binding;

        public OptionViewHolder(@NonNull ItemManageOptionSingleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position,
                         @NonNull SelectionOption selectionOption,
                         @NonNull Consumer<Integer> onCheck,
                         @NonNull Consumer<SelectionOption> onDelete,
                         boolean checked,
                         boolean enabled) {
            binding.label.setText(selectionOption.getLabel());
            binding.label.addTextChangedListener((OnTextChangedListener) (content, i, i1, i2) -> selectionOption.setLabel(content.toString()));

            binding.checkbox.setChecked(checked);
            binding.checkbox.setOnCheckedChangeListener((v, newCheckedState) -> {
                if (newCheckedState) {
                    binding.getRoot().post(() -> onCheck.accept(position));
                }
            });

            binding.delete.setVisibility(enabled ? View.VISIBLE : View.GONE);
            binding.delete.setOnClickListener(v -> onDelete.accept(selectionOption));

            Stream.of(
                    binding.checkbox,
                    binding.label
            ).forEach(view -> view.setEnabled(enabled));

        }
    }
}
