package it.niedermann.nextcloud.tables.features.table.view.types.factories.text;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.types.ViewHolderFactory;
import it.niedermann.nextcloud.tables.features.table.view.types.viewholder.text.LinkCellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class LinkCellFactory extends ViewHolderFactory {

    public LinkCellFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @Override
    public CellViewHolder create(@NonNull ViewGroup parent) {
        return new LinkCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
    }
}
