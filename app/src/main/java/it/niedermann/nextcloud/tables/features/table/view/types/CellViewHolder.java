package it.niedermann.nextcloud.tables.features.table.view.types;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public abstract class CellViewHolder extends AbstractViewHolder {

    @NonNull
    protected final DefaultValueSupplier defaultValueSupplier;

    public CellViewHolder(@NonNull View itemView, @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(itemView);
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public abstract void bind(@NonNull FullData fullData, @NonNull Column column);

    @NonNull
    public Optional<QuickActionProvider> getQuickActionProvider() {
        return Optional.empty();
    }

    public record QuickActionProvider(@StringRes int title) {
    }
}
