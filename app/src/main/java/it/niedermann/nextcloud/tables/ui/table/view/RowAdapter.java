package it.niedermann.nextcloud.tables.ui.table.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.databinding.ItemRowBinding;

public class RowAdapter extends RecyclerView.Adapter<RowViewHolder> {

    private final Consumer<Row> onClickListener;
    @NonNull
    private List<?> items = Collections.emptyList();

    public RowAdapter(@NonNull Consumer<Row> onClickListener) {
        this.onClickListener = onClickListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowViewHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        final var row = items.get(position);
//        holder.bind(row, v -> onClickListener.accept(row));
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(@Nullable Object items) {
//        if (tableContent == null) {
//            this.tableContent = new TableContent();
//        } else {
//            this.tableContent = tableContent;
//        }
//        notifyDataSetChanged();
    }
}
