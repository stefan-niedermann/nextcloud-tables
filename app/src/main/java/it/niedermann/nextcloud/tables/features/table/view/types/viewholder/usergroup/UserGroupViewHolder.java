package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.usergroup;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.TableviewCellRichBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class UserGroupViewHolder extends CellViewHolder {

    protected final TableviewCellRichBinding binding;

    public UserGroupViewHolder(@NonNull TableviewCellRichBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData, @NonNull Column column) {
        final var userNames = Optional.of(fullData.getUserGroups())
                .map(List::stream)
                .map(userGroups -> userGroups
                        .map(UserGroup::getRemoteId)
                        .collect(Collectors.joining(", @", "@", "")));

        // TODO
//         binding.rich.setCurrentSingleSignOnAccount();
//        binding.rich.setMarkdownString(userNames);
        binding.rich.setMarkdownString(userNames.orElse(null));

        binding.rich.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.rich.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
