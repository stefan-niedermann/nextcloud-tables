package it.niedermann.nextcloud.tables.database.entity;

import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;

import java.util.Objects;

import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;
import it.niedermann.nextcloud.tables.model.NextcloudVersion;
import it.niedermann.nextcloud.tables.model.TablesVersion;

@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Table.class,
                        parentColumns = "id",
                        childColumns = "currentTable",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index(name = "IDX_ACCOUNT_URL", value = "url"),
                @Index(name = "IDX_ACCOUNT_USERNAME", value = "userName"),
                @Index(name = "IDX_ACCOUNT_ACCOUNTNAME", value = "accountName", unique = true),
                @Index(name = "IDX_ACCOUNT_CURRENT_TABLE", value = "currentTable")
        }
)
public class Account extends AbstractEntity {
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String url = "";
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String userName = "";
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String accountName = "";
    @Nullable
    private NextcloudVersion nextcloudVersion;
    @Nullable
    private TablesVersion tablesVersion;
    @ColorInt
    @ColumnInfo(defaultValue = "-16743735")
    private int color = Color.parseColor("#0082C9");
    @Nullable
    private String displayName;
    @Nullable
    private Long currentTable;

    public Account() {
        // Default constructor
    }

    public Account(@NonNull String url, @NonNull String username, @NonNull String accountName, @Nullable String displayName) {
        this(accountName, username, url);
        setDisplayName(displayName);
    }

    public Account(@NonNull String accountName, @NonNull String username, @NonNull String url) {
        setAccountName(accountName);
        setUserName(username);
        setUrl(url);
    }

    /**
     * @return The {@link #getAvatarUrl(int, String)} of this {@link Account}
     */
    public GlideUrl getAvatarUrl(@Px int size) {
        return getAvatarUrl(size, getUserName());
    }

    /**
     * @return a {@link GlideUrl} to fetch the avatar of the given <code>userName</code> from the instance of this {@link Account} via {@link Glide}.
     */
    public GlideUrl getAvatarUrl(@Px int size, @NonNull String userName) {
        return new SingleSignOnUrl(getAccountName(), getUrl() + "/index.php/avatar/" + Uri.encode(userName) + "/" + size);
    }

    public boolean isSupported() {
        return tablesVersion != null && tablesVersion.getMinor() >= 3;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    @NonNull
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(@NonNull String accountName) {
        this.accountName = accountName;
    }

    @Nullable
    public NextcloudVersion getNextcloudVersion() {
        return nextcloudVersion;
    }

    public void setNextcloudVersion(@Nullable NextcloudVersion nextcloudVersion) {
        this.nextcloudVersion = nextcloudVersion;
    }

    @Nullable
    public TablesVersion getTablesVersion() {
        return tablesVersion;
    }

    public void setTablesVersion(@Nullable TablesVersion tablesVersion) {
        this.tablesVersion = tablesVersion;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public Long getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(@Nullable Long currentTable) {
        this.currentTable = currentTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Account account = (Account) o;
        return color == account.color && url.equals(account.url) && userName.equals(account.userName) && accountName.equals(account.accountName) && Objects.equals(nextcloudVersion, account.nextcloudVersion) && Objects.equals(tablesVersion, account.tablesVersion) && Objects.equals(displayName, account.displayName) && Objects.equals(currentTable, account.currentTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), url, userName, accountName, nextcloudVersion, tablesVersion, color, displayName, currentTable);
    }
}
