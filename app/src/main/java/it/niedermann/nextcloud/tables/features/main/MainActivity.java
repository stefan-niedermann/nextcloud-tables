package it.niedermann.nextcloud.tables.features.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityMainBinding;
import it.niedermann.nextcloud.tables.features.about.AboutActivity;
import it.niedermann.nextcloud.tables.features.accountswitcher.AccountSwitcherDialog;
import it.niedermann.nextcloud.tables.features.column.manage.ManageColumnsActivity;
import it.niedermann.nextcloud.tables.features.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.features.exception.ExceptionHandler;
import it.niedermann.nextcloud.tables.features.importaccount.ImportAccountActivity;
import it.niedermann.nextcloud.tables.features.row.EditRowActivity;
import it.niedermann.nextcloud.tables.features.settings.PreferencesActivity;
import it.niedermann.nextcloud.tables.features.table.edit.EditTableActivity;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;
import it.niedermann.nextcloud.tables.util.AvatarUtil;
import it.niedermann.nextcloud.tables.util.EmojiDrawable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final AvatarUtil avatarUtil = new AvatarUtil();
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.navView, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.fab, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            final var defaultMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
            mlp.topMargin = insets.top + defaultMargin;
            mlp.leftMargin = insets.left + defaultMargin;
            mlp.bottomMargin = insets.bottom + defaultMargin;
            mlp.rightMargin = insets.right + defaultMargin;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.swipeRefreshLayout, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.swipeRefreshLayout.setPadding(insets.left, 0, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        final var toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getCurrentAccount().observe(this, account -> {
            if (account == null) {
                startActivity(ImportAccountActivity.createIntent(MainActivity.this));
            } else {
                Log.i(TAG, "New account set: " + account);

                binding.swipeRefreshLayout.setOnRefreshListener(() -> mainViewModel.synchronize(account)
                        .whenCompleteAsync((result, exception) -> {
                            if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                                ExceptionDialogFragment.newInstance(exception, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                            }
                        }, ContextCompat.getMainExecutor(this)));

                Glide
                        .with(binding.toolbar.getContext())
                        .load(avatarUtil.getAvatarUrl(account, binding.toolbar.getMenu().findItem(R.id.account_switcher).getIcon().getIntrinsicWidth()))
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

        mainViewModel.isUserInitiatedSynchronizationActive().observe(this, binding.swipeRefreshLayout::setRefreshing);
        mainViewModel.isSwipeToRefreshEnabled().observe(this, binding.swipeRefreshLayout::setEnabled);

        mainViewModel.isLoading().observe(this, loading -> {
            this.binding.loadingWrapper.setVisibility(loading ? View.VISIBLE : View.GONE);
            this.binding.fragment.setVisibility(!loading ? View.VISIBLE : View.GONE);
        });

        mainViewModel.getTables().observe(this, this::updateSidebarMenu);
        mainViewModel.getCurrentTable().observe(this, accountAndTable -> applyCurrentTable(accountAndTable.account(), accountAndTable.table()));

        binding.toolbar.setOnClickListener(view -> {
            if (FeatureToggle.SEARCH_IN_TABLE.enabled) {
                throw new UnsupportedOperationException();
            } else {
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyCurrentTable(@Nullable Account account, @Nullable Table table) {
        if (account != null && table != null) {
            binding.fab.setOnClickListener(v -> startActivity(EditRowActivity.createAddIntent(this, account, table)));
            binding.fab.setEnabled(true);
        }

        final var hasCreatePermission = Optional
                .ofNullable(table)
                .map(Table::hasCreatePermission)
                .orElse(false);

        binding.fab.setVisibility(hasCreatePermission ? View.VISIBLE : View.GONE);
        binding.toolbar.setHint(table == null
                ? getString(R.string.choose_table_from_the_sidebar)
                : table.getTitleWithEmoji());
    }

    private void updateSidebarMenu(@Nullable MainViewModel.TablesPerAccount tables) {
        binding.navView.getMenu().clear();
        binding.navView.inflateMenu(R.menu.menu_main_navigation);
        final var menu = binding.navView.getMenu();

        if (tables == null) {
            Log.w(TAG, "Can not build sidenav menu because account is null");
            return;
        }

        addMenuGroup(menu, tables.getAccount(), getString(R.string.navigation_favorites), tables.getFavorites(), () -> updateSidebarMenu(tables));
        addMenuGroup(menu, tables.getAccount(), getString(R.string.navigation_tables), tables.getTables(), () -> updateSidebarMenu(tables));
        addMenuGroup(menu, tables.getAccount(), getString(R.string.navigation_archived), tables.getArchived(), () -> updateSidebarMenu(tables));

        // setActionView required to avoid inconsistencies when menu item order or grouping changes (table context menu appears next to static items)

        menu.findItem(R.id.add_table)
                .setActionView(new View(this))
                .setOnMenuItemClickListener(item -> {
                    startActivity(EditTableActivity.createIntent(MainActivity.this, tables.getAccount()));
                    return true;
                });
        menu.findItem(R.id.preferences)
                .setActionView(new View(this))
                .setOnMenuItemClickListener(item -> {
                    startActivity(PreferencesActivity.createIntent(MainActivity.this, tables.getAccount()));
                    return true;
                });
        menu.findItem(R.id.about)
                .setActionView(new View(this))
                .setOnMenuItemClickListener(item -> {
                    startActivity(AboutActivity.createIntent(this));
                    return true;
                });
    }

    /// @param onMenuItemChanged can be called optimistically when the table menu items will change, for example reordering or regrouping
    private void addMenuGroup(@NonNull Menu menu,
                              @NonNull Account account,
                              @NonNull String groupName,
                              @NonNull List<Table> tables,
                              @NonNull Runnable onMenuItemChanged) {
        if (tables.isEmpty()) {
            return;
        }

        final var subMenu = menu.addSubMenu(groupName);
        for (int i = 0; i < tables.size(); i++) {
            final var table = tables.get(i);
            final AppCompatImageButton contextMenu;
            if (table.hasManagePermission()) {
                contextMenu = new AppCompatImageButton(this);
                contextMenu.setBackgroundDrawable(null);
                contextMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_menu));
                contextMenu.setOnClickListener((v) -> {
                    final var popup = new TableContextPopupMenu(this, contextMenu, table);
                    popup.setOnMenuItemClickListener(item -> {
                        final var id = item.getItemId();

                        if (id == R.id.edit_table) {
                            startActivity(EditTableActivity.createIntent(MainActivity.this, account, table));
                            return true;

                        } else if (id == R.id.favorite_table) {
                            mainViewModel.toggleFavorite(account, table);
                            table.setFavorite(!table.isFavorite());
                            onMenuItemChanged.run();
                            return true;

                        } else if (id == R.id.archive_table) {
                            mainViewModel.toggleArchived(account, table);
                            table.setArchived(!table.isArchived());
                            onMenuItemChanged.run();
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

            final var tableMenuItem = subMenu
                    .add(Menu.NONE, 0, Menu.NONE, table.getTitle())
                    .setCheckable(true)
                    .setIcon(new EmojiDrawable(this, table.getEmoji()));

            if (contextMenu == null) {
                tableMenuItem
                        .setActionView(new View(this));

            } else {
                tableMenuItem
                        .setActionView(contextMenu)
                        .setOnMenuItemClickListener(item -> {
                            binding.drawerLayout.close();
                            binding.fab.setEnabled(false);
                            mainViewModel.setCurrentTable(account, table);
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
}