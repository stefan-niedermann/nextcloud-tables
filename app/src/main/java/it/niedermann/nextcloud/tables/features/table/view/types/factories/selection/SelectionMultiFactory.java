package it.niedermann.nextcloud.tables.features.table.view.types.factories.selection;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.types.ViewHolderFactory;
import it.niedermann.nextcloud.tables.features.table.view.types.viewholder.selection.SelectionMultiViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class SelectionMultiFactory extends ViewHolderFactory {

    public SelectionMultiFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @Override
    public CellViewHolder create(@NonNull ViewGroup parent) {
        return new SelectionMultiViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
    }
}
