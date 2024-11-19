package it.niedermann.nextcloud.tables.ui.table.view.types.factories.selection;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.ui.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.types.ViewHolderFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.viewholder.selection.SelectionCheckCellViewHolder;

public class SelectionCheckFactory extends ViewHolderFactory {

    public SelectionCheckFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @Override
    public CellViewHolder create(@NonNull ViewGroup parent) {
        return new SelectionCheckCellViewHolder(TableviewCellCheckBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
    }
}