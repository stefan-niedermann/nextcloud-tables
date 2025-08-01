package it.niedermann.nextcloud.tables.features.table.view.viewholder.types.usergroup;

import static java.util.function.Predicate.not;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.TableviewCellRichBinding;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class UserGroupViewHolder extends CellViewHolder {

    private static final Logger logger = Logger.getLogger(UserGroupViewHolder.class.getSimpleName());

    protected final TableviewCellRichBinding binding;

    public UserGroupViewHolder(@NonNull TableviewCellRichBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull Account account,
                     @NonNull FullData fullData,
                     @NonNull FullColumn fullColumn) {
        final var userNames = Optional.of(fullData.getUserGroups())
                .filter(not(List::isEmpty))
                .map(List::stream)
                .map(userGroups -> userGroups
                        .map(UserGroup::getRemoteId)
                        .collect(Collectors.joining(", @", "@", "")))
                .orElse(null);

        try {
            final var ssoAccount = AccountImporter.getSingleSignOnAccount(binding.getRoot().getContext(), account.getAccountName());
            binding.rich.setCurrentSingleSignOnAccount(ssoAccount, account.getColor());
            binding.rich.setMarkdownString(userNames);

        } catch (NextcloudFilesAppAccountNotFoundException e) {
            binding.rich.setMarkdownString(userNames);
            logger.log(Level.SEVERE, e.toString(), e);
        }

        binding.rich.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.rich.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    @Override
    public void bindPending() {
        binding.rich.setText(R.string.simple_loading);
    }
}
