package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;

public abstract class CellViewHolder extends AbstractViewHolder {

    public CellViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nullable Data data, @NonNull Column column);
}
