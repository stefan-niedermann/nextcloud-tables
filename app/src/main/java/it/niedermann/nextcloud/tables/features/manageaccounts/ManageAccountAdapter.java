package it.niedermann.nextcloud.tables.features.manageaccounts;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;

public class ManageAccountAdapter extends RecyclerView.Adapter<ManageAccountViewHolder> {

    @Nullable
    private Account currentAccount = null;
    @NonNull
    private final List<Account> accounts = new ArrayList<>();
    @NonNull
    private final Consumer<Account> onAccountClick;
    @NonNull
    private final Consumer<Pair<Account, Account>> onAccountDelete;

    public ManageAccountAdapter(@NonNull Consumer<Account> onAccountClick, @NonNull Consumer<Pair<Account, Account>> onAccountDelete) {
        this.onAccountClick = onAccountClick;
        this.onAccountDelete = onAccountDelete;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return accounts.get(position).getId();
    }

    @NonNull
    @Override
    public ManageAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageAccountViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAccountViewHolder holder, int position) {
        final var account = accounts.get(position);
        holder.bind(account, (clickedAccount) -> {
            setCurrentAccount(clickedAccount);
            onAccountClick.accept(clickedAccount);
        }, (accountToDelete -> {
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getId() == accountToDelete.getId()) {
                    accounts.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }

            final var newAccount = accounts.size() > 0 ? accounts.get(0) : null;
            setCurrentAccount(newAccount);
            onAccountDelete.accept(new Pair<>(accountToDelete, newAccount));
        }), currentAccount != null && currentAccount.getId() == account.getId());
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void setAccounts(@NonNull List<Account> accounts) {
        this.accounts.clear();
        this.accounts.addAll(accounts);
        notifyDataSetChanged();
    }

    public void setCurrentAccount(@Nullable Account currentAccount) {
        this.currentAccount = currentAccount;
        notifyDataSetChanged();
    }
}
