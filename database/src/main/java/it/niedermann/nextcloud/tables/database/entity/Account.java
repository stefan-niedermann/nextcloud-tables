package it.niedermann.nextcloud.tables.database.entity;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.model.NextcloudVersion;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;

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

    public Account(@NonNull String url,
                   @NonNull String accountName,
                   @NonNull String username) {
        this(url, accountName, username, null);
    }

    public Account(@NonNull String url,
                   @NonNull String accountName,
                   @NonNull String username,
                   @Nullable String displayName) {
        this.accountName = accountName;
        this.userName = username;
        this.url = url;
        this.displayName = displayName;
    }

    @Ignore
    public Account(@NonNull Account account) {
        super(account);
        this.accountName = account.getAccountName();
        this.userName = account.getUserName();
        this.url = account.getUrl();
        this.displayName = account.getDisplayName();
        this.nextcloudVersion = account.getNextcloudVersion();
        this.tablesVersion = account.getTablesVersion();
        this.color = account.getColor();
        this.currentTable = account.getCurrentTable();
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
    @NonNull
    public String toString() {
        return accountName;
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
