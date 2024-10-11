package it.niedermann.nextcloud.tables.repository;

import static java.util.concurrent.CompletableFuture.runAsync;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceLongLiveData;
import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.NextcloudVersion;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Version;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.ocs.OcsAPI;
import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.ocs.OcsVersionMapper;

@MainThread
public class AccountRepository extends AbstractRepository {

    private static final String TAG = AccountRepository.class.getSimpleName();
    private static final String SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT = "it.niedermann.nextcloud.tables.current_account";
    private final Context context;
    private final TablesDatabase db;
    private final ServerErrorHandler serverErrorHandler;
    private final SharedPreferences sharedPreferences;
    @SuppressWarnings("FieldCanBeLocal")
    private final LiveData<Long> currentAccountId$;
    private final LiveData<Account> currentAccount$;
    private final Mapper<OcsCapabilitiesResponse.OcsVersion, Version> versionMapper;

    public AccountRepository(@NonNull Context context) {
        this.context = context;
        this.db = TablesDatabase.getInstance(context);
        this.serverErrorHandler = new ServerErrorHandler(context);
        this.versionMapper = new OcsVersionMapper();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.currentAccountId$ = new ReactiveLiveData<>(new SharedPreferenceLongLiveData(sharedPreferences, SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT, -1L))
                .distinctUntilChanged();
        this.currentAccount$ = new ReactiveLiveData<>(currentAccountId$)
                .flatMap(currentAccountId -> currentAccountId < 1
                        ? new MutableLiveData<>(null)
                        : db.getAccountDao().getAccountById$(currentAccountId))
                .distinctUntilChanged();
    }

    @NonNull
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

    @AnyThread
    @NonNull
    public CompletableFuture<Account> createAccount(@NonNull Account account) {
        return CompletableFuture.supplyAsync(() -> {
            final long id = db.getAccountDao().insert(account);
            account.setId(id);
            return account;
        }, dbSequentialExecutor);
    }

    @NonNull
    public LiveData<List<Account>> getAccounts$() {
        return db.getAccountDao().getAccounts$();
    }

    @WorkerThread
    public List<Account> getAccounts() {
        return db.getAccountDao().getAccounts();
    }

    @NonNull
    public LiveData<List<Account>> getAccountsExcept$(long id) {
        return db.getAccountDao().getAccountsExcept$(id);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> setCurrentTable(long accountId, @Nullable Long tableId) {
        final var a = dbSequentialExecutor.submit(() -> db.getAccountDao().updateCurrentTable(accountId, tableId));
        return runAsync(() -> db.getAccountDao().updateCurrentTable(accountId, tableId), dbSequentialExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Account> synchronizeAccount(@NonNull Account account) {
        return CompletableFuture.supplyAsync(() -> {
                    try (final var apiProvider = ApiProvider.getOcsApiProvider(context, account)) {
                        final var syncedAccount = synchronizeCapabilities(apiProvider.getApi(), account);
                        return synchronizeUser(apiProvider.getApi(), syncedAccount);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor)
                .thenApplyAsync(entity -> {
                    db.getAccountDao().update(entity);
                    return entity;
                }, dbSequentialExecutor);
    }

    @WorkerThread
    @NonNull
    private Account synchronizeCapabilities(@NonNull OcsAPI api, @NonNull Account account) throws Exception {
        final var response = api.getCapabilities(account.getETag()).execute();

        //noinspection SwitchStatementWithTooFewBranches
        switch (response.code()) {
            case 200 -> {
                final var body = response.body();
                if (body == null) {
                    throw new IOException("Response body is null");
                }

                switch (body.ocs.meta.statusCode) {
                    case 500 ->
                            throw new ServerNotAvailableException(ServerNotAvailableException.Reason.SERVER_ERROR);
                    case 503 ->
                            throw new ServerNotAvailableException(ServerNotAvailableException.Reason.MAINTENANCE_MODE);
                    default -> {
                    }
                }

                final var nextcloudVersion = NextcloudVersion.of(versionMapper.toEntity(body.ocs.data.version()));
                if (!nextcloudVersion.isSupported()) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.TABLES_NOT_SUPPORTED);
                }

                final var tablesNode = body.ocs.data.capabilities().tables();
                if (tablesNode == null) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.NOT_INSTALLED);
                }
                if (!tablesNode.enabled()) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.NOT_ENABLED);
                }

                final var tablesVersion = TablesVersion.parse(tablesNode.version());
                if (!tablesVersion.isSupported()) {
                    throw new ServerNotAvailableException(ServerNotAvailableException.Reason.TABLES_NOT_SUPPORTED);
                }

                account.setTablesVersion(tablesVersion);
                account.setNextcloudVersion(nextcloudVersion);
                account.setETag(response.headers().get("ETag"));
                account.setColor(Color.parseColor(ColorUtil.formatColorToParsableHexString(body.ocs.data.capabilities().theming().color)));
            }
            default -> {
                final var exception = serverErrorHandler.responseToException(response, "Could not fetch capabilities " + account.getUserName(), true);

                if (exception.isPresent()) {
                    throw exception.get();
                }
            }
        }

        return account;
    }

    @WorkerThread
    @NonNull
    private Account synchronizeUser(@NonNull OcsAPI api, @NonNull Account account) throws Exception {
        final var response = api.getUser(account.getUserName()).execute();

        //noinspection SwitchStatementWithTooFewBranches
        switch (response.code()) {
            case 200 -> {
                final var body = response.body();
                if (body == null) {
                    throw new RuntimeException("Response body is null");
                }

                account.setDisplayName(body.ocs.data.displayName);
            }
            default -> {
                final var exception = serverErrorHandler.responseToException(response, "Could not fetch user " + account.getUserName(), true);

                if (exception.isPresent()) {
                    throw exception.get();
                }
            }
        }

        return account;
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteAccount(@NonNull Account account) {
        return runAsync(() -> db.getAccountDao().delete(account), dbSequentialExecutor);
    }

    public CompletableFuture<Table> guessCurrentTable(@NonNull Account account) {
        try {
            dbParallelExecutor.submit(() -> db.getTableDao().getAnyNotDeletedTable(account.getId())).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        return CompletableFuture
                .supplyAsync(() -> db.getTableDao().getAnyNotDeletedTable(account.getId()), dbParallelExecutor)
                .thenComposeAsync(table -> {
                    if (table != null) {
                        db.getAccountDao().updateCurrentTable(account.getId(), table.getId());
                    }
                    return CompletableFuture.completedFuture(table);
                }, dbSequentialExecutor);
    }
}
