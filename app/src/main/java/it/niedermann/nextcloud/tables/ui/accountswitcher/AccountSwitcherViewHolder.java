package it.niedermann.nextcloud.tables.ui.accountswitcher;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.ItemAccountChooseBinding;
import it.niedermann.nextcloud.tables.util.AvatarUtil;

public class AccountSwitcherViewHolder extends RecyclerView.ViewHolder {

    private final AvatarUtil avatarUtil = new AvatarUtil();
    private final ItemAccountChooseBinding binding;

    public AccountSwitcherViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemAccountChooseBinding.bind(itemView);
    }

    public void bind(@NonNull Account account, @NonNull Consumer<Account> onAccountClick) {
        binding.accountName.setText(
                TextUtils.isEmpty(account.getDisplayName())
                        ? account.getUserName()
                        : account.getDisplayName()
        );
        binding.accountHost.setText(Uri.parse(account.getUrl()).getHost());
        Glide.with(itemView.getContext())
                .load(avatarUtil.getAvatarUrl(account, binding.accountItemAvatar.getResources().getDimensionPixelSize(R.dimen.avatar_size)))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)
                .into(binding.accountItemAvatar);
        itemView.setOnClickListener((v) -> onAccountClick.accept(account));
        binding.delete.setVisibility(View.GONE);
    }
}
