package it.niedermann.nextcloud.tables.binding;

import static it.niedermann.nextcloud.tables.util.AvatarUtil.getAvatarUrl;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;

public class AccountBinding {

    private AccountBinding() {
        // Util class
    }

    @BindingAdapter({"account"})
    public static void setAvatar(@NonNull ImageView view, @Nullable Account account) {
        Glide.with(view)
                .load(account == null ? R.mipmap.ic_launcher : getAvatarUrl(account, view.getWidth()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(view);
    }
}
