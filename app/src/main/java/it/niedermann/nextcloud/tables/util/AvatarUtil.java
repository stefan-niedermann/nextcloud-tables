package it.niedermann.nextcloud.tables.util;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;

import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;
import it.niedermann.nextcloud.tables.database.entity.Account;

public class AvatarUtil {

    private AvatarUtil() {
        // Util class
    }

    /**
     * @return The {@link #getAvatarUrl(Account, int, String)} of this {@link Account}
     */
    public static GlideUrl getAvatarUrl(@NonNull Account account, @Px int size) {
        return getAvatarUrl(account, size, account.getUserName());
    }

    /**
     * @return a {@link GlideUrl} to fetch the avatar of the given <code>userName</code> from the instance of this {@link Account} via {@link Glide}.
     */
    public static GlideUrl getAvatarUrl(@NonNull Account account, @Px int size, @NonNull String userName) {
        return new SingleSignOnUrl(account.getAccountName(), account.getUrl() + "/index.php/avatar/" + Uri.encode(userName) + "/" + size);
    }
}
