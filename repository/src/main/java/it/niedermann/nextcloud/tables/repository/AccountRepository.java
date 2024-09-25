package it.niedermann.nextcloud.tables.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import it.niedermann.android.sharedpreferences.SharedPreferenceLongLiveData;
import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.NextcloudVersion;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.api.OcsAPI;
import it.niedermann.nextcloud.tables.remote.exception.ServerNotAvailableException;

@WorkerThread
public class AccountRepository {

    private static final String TAG = AccountRepository.class.getSimpleName();
    private static final String SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT = "it.niedermann.nextcloud.tables.current_account";
    private final Context context;
    private final TablesDatabase db;
    private final ServerErrorHandler serverErrorHandler;
    private final SharedPreferences sharedPreferences;
    @SuppressWarnings("FieldCanBeLocal")
    private final LiveData<Long> currentAccountId$;
    private final LiveData<Account> currentAccount$;

    @MainThread
    public AccountRepository(@NonNull Context context) {
        this.context = context;
        this.db = TablesDatabase.getInstance(context);
        this.serverErrorHandler = new ServerErrorHandler(context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.currentAccountId$ = new SharedPreferenceLongLiveData(sharedPreferences, SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT, -1L);
        this.currentAccount$ = Transformations.distinctUntilChanged(Transformations.switchMap(currentAccountId$, currentAccountId -> (currentAccountId < 0)
                ? new MutableLiveData<>(null)
                : db.getAccountDao().getAccountById$(currentAccountId)));
    }

    @MainThread
    public LiveData<Account> getCurrentAccount() {
        return currentAccount$;
    }

    public void setCurrentAccount(@Nullable Account account) {
        final var editor = this.sharedPreferences.edit();
        if (account == null) {
            Log.i(TAG, "Setting current account to null. Maybe last account has been deleted?");
            editor.remove(SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT);
        } else {
            editor.putLong(SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT, account.getId());
        }
        editor.apply();
    }

    public Account createAccount(@NonNull Account account) {
        final var id = db.getAccountDao().insert(account);
        return db.getAccountDao().getAccountById(id);
    }

    @MainThread
    public LiveData<List<Account>> getAccounts$() {
        return db.getAccountDao().getAccounts$();
    }

    public List<Account> getAccounts() {
        return db.getAccountDao().getAccounts();
    }

    @MainThread
    public LiveData<List<Account>> getAccountsExcept$(long id) {
        return db.getAccountDao().getAccountsExcept$(id);
    }

    public void setCurrentTable(long accountId, @Nullable Long tableId) {
        db.getAccountDao().updateCurrentTable(accountId, tableId);
    }

    public void synchronizeAccount(@NonNull Account account) throws Exception {
        try (final var apiProvider = ApiProvider.getOcsApiProvider(context, account)) {
            synchronizeCapabilities(apiProvider.getApi(), account);
            synchronizeUser(apiProvider.getApi(), account);
            db.getAccountDao().update(account);
        }
    }

    private void synchronizeCapabilities(@NonNull OcsAPI api, @NonNull Account account) throws Exception {
        final var response = api.getCapabilities(account.getETag()).execute();
        //noinspection SwitchStatementWithTooFewBranches
        switch (response.code()) {
            case 200: {
                final var body = response.body();
                if (body == null) {
                    throw new IOException("Response body is null");
                }

                switch (body.ocs.meta.statusCode) {
                    case 500:
                        throw new ServerNotAvailableException(ServerNotAvailableException.Reason.SERVER_ERROR);
                    case 503:
                        throw new ServerNotAvailableException(ServerNotAvailableException.Reason.MAINTENANCE_MODE);
                    default:
                        break;
                }

                final var nextcloudVersion = NextcloudVersion.of(body.ocs.data.version);
                if (!nextcloudVersion.isSupported()) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.TABLES_NOT_SUPPORTED);
                }

                final var tablesNode = body.ocs.data.capabilities.tables;
                if (tablesNode == null) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.NOT_INSTALLED);
                }
                if (!tablesNode.enabled) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.NOT_ENABLED);
                }

                final var tablesVersion = TablesVersion.parse(tablesNode.version);
                if (!tablesVersion.isSupported()) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.TABLES_NOT_SUPPORTED);
                }

                account.setTablesVersion(tablesVersion);
                account.setNextcloudVersion(nextcloudVersion);
                account.setETag(response.headers().get("ETag"));
                account.setColor(Color.parseColor(ColorUtil.formatColorToParsableHexString(body.ocs.data.capabilities.theming.color)));
                break;
            }

            default: {
                serverErrorHandler.handle(response);
                break;
            }
        }
    }

    private void synchronizeUser(@NonNull OcsAPI api, @NonNull Account account) throws Exception {
        final var response = api.getUser(account.getUserName()).execute();
        //noinspection SwitchStatementWithTooFewBranches
        switch (response.code()) {
            case 200: {
                final var body = response.body();
                if (body == null) {
                    throw new RuntimeException("Response body is null");
                }

                account.setDisplayName(body.ocs.data.displayName);
                break;
            }

            default: {
                serverErrorHandler.handle(response, "Could not fetch user " + account.getUserName());
                break;
            }
        }
    }

    public void deleteAccount(@NonNull Account account) {
        db.getAccountDao().delete(account);
    }

    public void guessCurrentTable(@NonNull Account account) {
        Optional.ofNullable(db.getTableDao().getAnyNotDeletedTables(account.getId()))
                .ifPresent(table -> db.getAccountDao().updateCurrentTable(account.getId(), table.getId()));
    }
}
