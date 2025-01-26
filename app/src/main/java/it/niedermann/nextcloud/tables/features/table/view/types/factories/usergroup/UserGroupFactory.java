package it.niedermann.nextcloud.tables.features.table.view.types.factories.usergroup;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.databinding.TableviewCellRichBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.types.ViewHolderFactory;
import it.niedermann.nextcloud.tables.features.table.view.types.viewholder.usergroup.UserGroupViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class UserGroupFactory extends ViewHolderFactory {

    public UserGroupFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @Override
    public CellViewHolder create(@NonNull ViewGroup parent) {
        return new UserGroupViewHolder(TableviewCellRichBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
    }
}
