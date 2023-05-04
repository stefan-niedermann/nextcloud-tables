package it.niedermann.nextcloud.tables.ui.column.manage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.ItemColumnBinding;

public class ManageColumnsAdapter extends RecyclerView.Adapter<ManageColumnsViewHolder> {

    private final List<Column> columns = new ArrayList<>();
    private final Consumer<Column> onEdit;

    public ManageColumnsAdapter(@NonNull Consumer<Column> onEdit) {
        this.onEdit = onEdit;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ManageColumnsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageColumnsViewHolder(ItemColumnBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageColumnsViewHolder holder, int position) {
        holder.bind(columns.get(position), onEdit);
    }

    @Override
    public long getItemId(int position) {
        return columns.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return columns.size();
    }

    public void setItems(@NonNull Collection<Column> columns) {
        this.columns.clear();
        this.columns.addAll(columns);
        notifyDataSetChanged();
    }
}
