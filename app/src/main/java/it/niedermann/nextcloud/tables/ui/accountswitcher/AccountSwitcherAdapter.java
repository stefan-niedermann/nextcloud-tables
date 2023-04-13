package it.niedermann.nextcloud.tables.ui.accountswitcher;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;

public class AccountSwitcherAdapter extends RecyclerView.Adapter<AccountSwitcherViewHolder> {

    @NonNull
    private final List<Account> accounts = new ArrayList<>();
    @NonNull
    private final Consumer<Account> onAccountClick;

    public AccountSwitcherAdapter(@NonNull Consumer<Account> onAccountClick) {
        this.onAccountClick = onAccountClick;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return accounts.get(position).getId();
    }

    @NonNull
    @Override
    public AccountSwitcherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountSwitcherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountSwitcherViewHolder holder, int position) {
        holder.bind(accounts.get(position), onAccountClick);
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
}
