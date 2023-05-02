package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;

public abstract class CellViewHolder extends AbstractViewHolder {

    public CellViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nullable Data data, @NonNull Column column);

    @NonNull
    public Optional<QuickActionProvider> getQuickActionProvider() {
        return Optional.empty();
    }

    public static class QuickActionProvider {

        @StringRes
        private final int title;

        public QuickActionProvider(@StringRes int title) {
            this.title = title;
        }

        @StringRes
        public int getTitle() {
            return title;
        }
    }
}
