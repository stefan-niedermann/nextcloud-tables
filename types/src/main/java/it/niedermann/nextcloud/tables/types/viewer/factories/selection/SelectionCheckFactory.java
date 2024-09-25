package it.niedermann.nextcloud.tables.types.viewer.factories.selection;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.viewholder.selection.SelectionCheckCellViewHolder;

public class SelectionCheckFactory extends ViewHolderFactory {

    public SelectionCheckFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @Override
    public CellViewHolder create(@NonNull ViewGroup parent) {
        return new SelectionCheckCellViewHolder(TableviewCellCheckBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
    }
}
