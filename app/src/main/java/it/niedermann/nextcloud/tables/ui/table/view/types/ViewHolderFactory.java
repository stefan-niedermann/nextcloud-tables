package it.niedermann.nextcloud.tables.ui.table.view.types;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public abstract class ViewHolderFactory {

    @NonNull
    protected final DefaultValueSupplier defaultValueSupplier;

    protected ViewHolderFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public abstract CellViewHolder create(@NonNull ViewGroup parent);
}
