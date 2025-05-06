package it.niedermann.nextcloud.tables.features.column.manage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import it.niedermann.nextcloud.tables.database.entity.AbstractEntity;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ItemColumnBinding;

public class ManageColumnsAdapter extends RecyclerView.Adapter<ManageColumnsViewHolder> {

    private final List<FullColumn> fullColumns = new ArrayList<>();
    private final Consumer<FullColumn> onEdit;

    public ManageColumnsAdapter(@NonNull Consumer<FullColumn> onEdit) {
        this.onEdit = onEdit;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ManageColumnsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageColumnsViewHolder(ItemColumnBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageColumnsViewHolder holder, int position) {
        holder.bind(fullColumns.get(position), onEdit);
    }

    @Override
    public long getItemId(int position) {
        return fullColumns.get(position).getColumn().getId();
    }

    @Override
    public int getItemCount() {
        return fullColumns.size();
    }

    public void setItems(@NonNull Collection<FullColumn> fullColumns) {
        this.fullColumns.clear();
        this.fullColumns.addAll(fullColumns);
        notifyDataSetChanged();
    }

    /**
     * Swaps the items only in-memory without persisting the new positions.
     *
     * @return whether or not the items could be swapped successfully.
     */
    public boolean swapVolatile(int fromPosition, int toPosition) {
        try {
            Collections.swap(fullColumns, fromPosition, toPosition);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public List<Long> getColumnIdsOrderedByViewPosition() {
        return this.fullColumns.stream()
                .map(FullColumn::getColumn)
                .map(AbstractEntity::getId)
                .toList();
    }
}
