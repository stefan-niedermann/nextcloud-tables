package it.niedermann.nextcloud.tables.ui.main;

import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.TablesApplication.FeatureToggle;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityMainBinding;
import it.niedermann.nextcloud.tables.ui.about.AboutActivity;
import it.niedermann.nextcloud.tables.ui.accountswitcher.AccountSwitcherDialog;
import it.niedermann.nextcloud.tables.ui.column.manage.ManageColumnsActivity;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.tables.ui.importaccount.ImportAccountActivity;
import it.niedermann.nextcloud.tables.ui.settings.PreferencesActivity;
import it.niedermann.nextcloud.tables.ui.table.edit.EditTableActivity;
import it.niedermann.nextcloud.tables.ui.util.EmojiDrawable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setTheme(R.style.AppTheme);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final var toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getCurrentAccount().observe(this, account -> {
            if (account == null) {
                startActivity(ImportAccountActivity.createIntent(MainActivity.this));
            } else {
                Log.i(TAG, "New account set: " + account);

                Glide
                        .with(binding.toolbar.getContext())
                        .load(account.getAvatarUrl(binding.toolbar.getMenu().findItem(R.id.account_switcher).getIcon().getIntrinsicWidth()))
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                binding.toolbar.getMenu().findItem(R.id.account_switcher).setIcon(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        });

        mainViewModel.getTables().observe(this, this::updateSidebarMenu);
        mainViewModel.getCurrentTable().observe(this, this::applyCurrentTable);

        binding.toolbar.setOnClickListener(view -> {
            if (FeatureToggle.SEARCH_IN_TABLE.enabled) {
                throw new UnsupportedOperationException();
            } else {
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });

        final var connectivityManager = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        final var networkCallbackReference = new AtomicReference<ConnectivityManager.NetworkCallback>();
        mainViewModel.getAccountAndNetworkRequest().observe(this, accountAndNetworkRequest -> {
            if (networkCallbackReference.get() != null) {
                connectivityManager.unregisterNetworkCallback(networkCallbackReference.get());
            }
            networkCallbackReference.set(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    Log.i(TAG, "Network available, trigger synchronization for " + accountAndNetworkRequest.first);
                    mainViewModel.synchronizeAccountAndTables(accountAndNetworkRequest.first).whenCompleteAsync((result, exception) -> {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    }, ContextCompat.getMainExecutor(MainActivity.this));
                }
            });
            connectivityManager.requestNetwork(accountAndNetworkRequest.second, networkCallbackReference.get());
        });
    }

    private void applyCurrentTable(@Nullable Table table) {
        binding.toolbar.setHint(table == null
                ? getString(R.string.choose_table_from_the_sidebar)
                : table.getTitleWithEmoji());
    }

    private void updateSidebarMenu(@Nullable MainViewModel.TablesPerAccount tables) {
        final var menu = binding.navView.getMenu();
        menu.clear();

        if (tables == null) {
            Log.w(TAG, "Can not build sidenav menu because account is null");
            return;
        }

        addMenuGroup(menu, tables.getAccount(), getString(R.string.navigation_my_tables), tables.getOwnTables());
        addMenuGroup(menu, tables.getAccount(), getString(R.string.navigation_shared_tables), tables.getSharedTables());

        menu.add(Menu.NONE, EMenuItem.ADD_TABLE.id, Menu.NONE, R.string.add_table)
                .setIcon(R.drawable.ic_baseline_add_24)
                .setOnMenuItemClickListener(item -> {
                    startActivity(EditTableActivity.createIntent(MainActivity.this, tables.getAccount()));
                    return true;
                });
        menu.add(Menu.NONE, EMenuItem.PREFERENCES.id, Menu.NONE, R.string.simple_settings)
                .setIcon(R.drawable.ic_baseline_settings_24)
                .setOnMenuItemClickListener(item -> {
                    startActivity(PreferencesActivity.createIntent(MainActivity.this, tables.getAccount()));
                    return true;
                });
        menu.add(Menu.NONE, EMenuItem.ABOUT.id, Menu.NONE, R.string.simple_about)
                .setIcon(R.drawable.ic_outline_info_24)
                .setOnMenuItemClickListener(item -> {
                    startActivity(AboutActivity.createIntent(this, tables.getAccount()));
                    return true;
                });
    }

    private void addMenuGroup(@NonNull Menu menu, @NonNull Account account, @NonNull String groupName, @NonNull List<Table> tables) {
        if (!tables.isEmpty()) {
            final var sharedTables = menu.addSubMenu(groupName);
            for (int i = 0; i < tables.size(); i++) {
                final var table = tables.get(i);

                final AppCompatImageButton contextMenu;
                if (table.hasManagePermission()) {
                    contextMenu = new AppCompatImageButton(this);
                    contextMenu.setBackgroundDrawable(null);
                    contextMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_menu));
                    contextMenu.setOnClickListener((v) -> {
                        final var popup = new PopupMenu(this, contextMenu);
                        popup.getMenuInflater().inflate(R.menu.context_menu_table, popup.getMenu());
                        popup.setOnMenuItemClickListener(item -> {
                            final var id = item.getItemId();
                            if (id == R.id.edit_table) {
                                startActivity(EditTableActivity.createIntent(MainActivity.this, account, table));
                                return true;
                            } else if (id == R.id.share_table) {
                                if (FeatureToggle.SHARE_TABLE.enabled) {
                                    throw new UnsupportedOperationException();
                                } else {
                                    Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            } else if (id == R.id.manage_columns) {
                                startActivity(ManageColumnsActivity.createIntent(this, account, table));
                                return true;
                            } else if (id == R.id.delete_table) {
                                new MaterialAlertDialogBuilder(this)
                                        .setTitle(getString(R.string.delete_item, table.getTitle()))
                                        .setMessage(getString(R.string.delete_item_message, table.getTitle()))
                                        .setPositiveButton(R.string.simple_delete, (dialog, which) -> {
                                            mainViewModel.deleteTable(table).whenCompleteAsync((result, exception) -> {
                                                if (exception != null) {
                                                    ExceptionDialogFragment.newInstance(exception, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                                }
                                            }, ContextCompat.getMainExecutor(this));
                                        })
                                        .setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                                        .show();
                                return true;
                            }
                            return false;
                        });
                        popup.show();
                    });
                } else {
                    contextMenu = null;
                }

                sharedTables.add(Menu.NONE, i, Menu.NONE, table.getTitle())
                        .setCheckable(true)
                        .setIcon(new EmojiDrawable(this, table.getEmoji()))
                        .setActionView(contextMenu)
                        .setOnMenuItemClickListener(item -> {
                            binding.drawerLayout.close();
                            mainViewModel.setCurrentTable(table);
                            return true;
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final var id = item.getItemId();
        if (id == R.id.account_switcher) {
            AccountSwitcherDialog.newInstance().show(getSupportFragmentManager(), AccountSwitcherDialog.class.getSimpleName());
        }
        return super.onOptionsItemSelected(item);
    }

    private enum EMenuItem {
        ADD_TABLE(-1),
        PREFERENCES(-2),
        ABOUT(-3),
        ;

        private final int id;

        EMenuItem(int id) {
            this.id = id;
        }
    }
}