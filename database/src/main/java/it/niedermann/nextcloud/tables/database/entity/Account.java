package it.niedermann.nextcloud.tables.database.entity;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.DBStatus;
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
                @Index(value = "user_status"),
                @Index(value = "capabilities_status"),
                @Index(value = {"userName", "url"}, unique = true),
                @Index(value = "accountName", unique = true),
                @Index(value = "currentTable")
        }
)
public class Account extends AbstractEntity {

    @NonNull
    @Embedded(prefix = "user_")
    private SynchronizationContext userSynchronizationContext;

    @NonNull
    @Embedded(prefix = "capabilities_")
    private SynchronizationContext capabilitiesSynchronizationContext;

    @NonNull
    @Embedded(prefix = "search_")
    private SynchronizationContext searchProviderSynchronizationContext;

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
        this("", "", "");
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
        this.userSynchronizationContext = new SynchronizationContext();
        this.capabilitiesSynchronizationContext = new SynchronizationContext();
        this.searchProviderSynchronizationContext = new SynchronizationContext();
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
        this.userSynchronizationContext = new SynchronizationContext(account.getUserSynchronizationContext());
        this.capabilitiesSynchronizationContext = new SynchronizationContext(account.getCapabilitiesSynchronizationContext());
        this.searchProviderSynchronizationContext = new SynchronizationContext(account.getSearchProviderSynchronizationContext());
    }

    @NonNull
    public SynchronizationContext getUserSynchronizationContext() {
        return userSynchronizationContext;
    }

    public void setUserSynchronizationContext(@NonNull SynchronizationContext userSynchronizationContext) {
        this.userSynchronizationContext = userSynchronizationContext;
    }

    @NonNull
    public SynchronizationContext getCapabilitiesSynchronizationContext() {
        return capabilitiesSynchronizationContext;
    }

    public void setCapabilitiesSynchronizationContext(@NonNull SynchronizationContext capabilitiesSynchronizationContext) {
        this.capabilitiesSynchronizationContext = capabilitiesSynchronizationContext;
    }

    @NonNull
    public SynchronizationContext getSearchProviderSynchronizationContext() {
        return searchProviderSynchronizationContext;
    }

    public void setSearchProviderSynchronizationContext(@NonNull SynchronizationContext searchProviderSynchronizationContext) {
        this.searchProviderSynchronizationContext = searchProviderSynchronizationContext;
    }

    @NonNull
    @Ignore
    public DBStatus getUserStatus() {
        return Optional.ofNullable(userSynchronizationContext.status()).orElse(DBStatus.VOID);
    }

    @Ignore
    public void setUserStatus(@NonNull DBStatus status) {
        userSynchronizationContext = new SynchronizationContext(status, userSynchronizationContext.eTag());
    }

    @Nullable
    @Ignore
    public String getUserETag() {
        return userSynchronizationContext.eTag();
    }

    @Ignore
    public void setUserETag(@NonNull String eTag) {
        userSynchronizationContext = new SynchronizationContext(userSynchronizationContext.status(), eTag);
    }

    @NonNull
    @Ignore
    public DBStatus getCapabilitiesStatus() {
        return Optional.ofNullable(capabilitiesSynchronizationContext.status()).orElse(DBStatus.VOID);
    }

    @Ignore
    public void setCapabilitiesStatus(@Nullable DBStatus status) {
        capabilitiesSynchronizationContext = new SynchronizationContext(status, capabilitiesSynchronizationContext.eTag());
    }

    @Nullable
    @Ignore
    public String getCapabilitiesETag() {
        return capabilitiesSynchronizationContext.eTag();
    }

    @Ignore
    public void setCapabilitiesETag(@Nullable String eTag) {
        capabilitiesSynchronizationContext = new SynchronizationContext(capabilitiesSynchronizationContext.status(), eTag);
    }

    @NonNull
    @Ignore
    public DBStatus getSearchProviderStatus() {
        return Optional.ofNullable(searchProviderSynchronizationContext.status()).orElse(DBStatus.VOID);
    }

    @Ignore
    public void setSearchProviderStatus(@Nullable DBStatus status) {
        searchProviderSynchronizationContext = new SynchronizationContext(status, searchProviderSynchronizationContext.eTag());
    }

    @Nullable
    @Ignore
    public String getSearchProviderETag() {
        return searchProviderSynchronizationContext.eTag();
    }

    @Ignore
    public void setSearchProviderETag(@Nullable String eTag) {
        searchProviderSynchronizationContext = new SynchronizationContext(searchProviderSynchronizationContext.status(), eTag);
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
        //noinspection DataFlowIssue
        return Stream.of(accountName, tablesVersion)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Account account = (Account) o;
        return color == account.color && Objects.equals(userSynchronizationContext, account.userSynchronizationContext) && Objects.equals(capabilitiesSynchronizationContext, account.capabilitiesSynchronizationContext) && Objects.equals(searchProviderSynchronizationContext, account.searchProviderSynchronizationContext) && Objects.equals(url, account.url) && Objects.equals(userName, account.userName) && Objects.equals(accountName, account.accountName) && Objects.equals(nextcloudVersion, account.nextcloudVersion) && Objects.equals(tablesVersion, account.tablesVersion) && Objects.equals(displayName, account.displayName) && Objects.equals(currentTable, account.currentTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userSynchronizationContext, capabilitiesSynchronizationContext, searchProviderSynchronizationContext, url, userName, accountName, nextcloudVersion, tablesVersion, color, displayName, currentTable);
    }
}
