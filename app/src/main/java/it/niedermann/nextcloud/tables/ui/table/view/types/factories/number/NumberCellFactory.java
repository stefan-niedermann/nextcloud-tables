package it.niedermann.nextcloud.tables.ui.table.view.types.factories.number;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.ui.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.types.ViewHolderFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.viewholder.number.NumberCellViewHolder;

public class NumberCellFactory extends ViewHolderFactory {

    public NumberCellFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @Override
    public CellViewHolder create(@NonNull ViewGroup parent) {
        return new NumberCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
    }
}
