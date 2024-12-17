package it.niedermann.nextcloud.tables.ui.table.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Collections;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.FragmentTableBinding;
import it.niedermann.nextcloud.tables.model.FullTable;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EPermissionV2Dto;
import it.niedermann.nextcloud.tables.repository.defaults.DataTypeDefaultServiceRegistry;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;
import it.niedermann.nextcloud.tables.ui.column.edit.EditColumnActivity;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.row.EditRowActivity;
import it.niedermann.nextcloud.tables.ui.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.types.DataTypeViewerServiceRegistry;
import it.niedermann.nextcloud.tables.ui.table.view.types.ViewHolderFactory;

public class ViewTableFragment extends Fragment {

    private static final String TAG = ViewTableFragment.class.getSimpleName();
    private FragmentTableBinding binding;
    private ViewTableViewModel viewTableViewModel;
    private TableViewAdapter adapter;
    private DataTypeServiceRegistry<ViewHolderFactory> registry;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registry = new DataTypeViewerServiceRegistry(new DataTypeDefaultServiceRegistry());
        binding = FragmentTableBinding.inflate(inflater, container, false);
        adapter = new TableViewAdapter(registry);
        binding.tableView.setAdapter(adapter);
        binding.tableView.getCellRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                binding.swipeRefreshLayout.setEnabled(binding.tableView.getCellLayoutManager().findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewTableViewModel = new ViewModelProvider(this).get(ViewTableViewModel.class);
        viewTableViewModel.isUserInitiatedSynchronizationActive().observe(getViewLifecycleOwner(), binding.swipeRefreshLayout::setRefreshing);
        viewTableViewModel.getCurrentFullTable$().observe(getViewLifecycleOwner(), pair -> {
            binding.tableView.getScrollHandler().scrollToRowPosition(0);
            binding.tableView.getScrollHandler().scrollToColumnPosition(0);
            applyCurrentTable(pair.first, pair.second);
        });
    }

    private void applyCurrentTable(@NonNull Account account, @Nullable FullTable fullTable) {
        if (fullTable == null) {
            Log.i(TAG, "Current table: " + null);
            adapter.setAllItems(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            binding.tableView.setTableViewListener(null);
            binding.fab.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setOnRefreshListener(null);
            return;
        }

        Log.i(TAG, "Current table: " + fullTable.table());

        binding.fab.setVisibility(fullTable.table().hasCreatePermission() ? View.VISIBLE : View.GONE);

        final var rowPosition = binding.tableView.getCellLayoutManager().findFirstVisibleItemPosition();
        final var columnPosition = binding.tableView.getColumnHeaderLayoutManager().findFirstVisibleItemPosition();

        // Workaround for https://github.com/stefan-niedermann/nextcloud-tables/issues/16
        if (fullTable.fullRows().isEmpty()) {
            adapter.setAllItems(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        } else {
            adapter.setAllItems(fullTable.fullColumns(), fullTable.fullRows(), fullTable.data());
        }

        binding.tableView.getCellLayoutManager().scrollToPosition(rowPosition);
        binding.tableView.getRowHeaderLayoutManager().scrollToPosition(rowPosition);
        binding.tableView.getColumnHeaderLayoutManager().scrollToPosition(columnPosition);

        binding.tableView.setTableViewListener(new DefaultTableViewListener() {
            @Override
            public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
                if (!fullTable.table().hasUpdatePermission()) {
                    Log.i(TAG, "Insufficient permissions: " + EPermissionV2Dto.UPDATE);
                    return;
                }

                if (rowPosition >= fullTable.fullRows().size()) {
                    final var exception = new IllegalStateException("Tried to access rowPosition " + rowPosition + " but there were only " + fullTable.fullRows().size() + " rows.");

                    if (FeatureToggle.STRICT_MODE.enabled) {
                        ExceptionDialogFragment.newInstance(exception, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    } else {
                        exception.printStackTrace();
                    }

                    return;
                }

                final var row = fullTable.fullRows().get(rowPosition);
                if (row == null) {
                    final var exception = new IllegalStateException("No row header at position " + rowPosition);

                    if (FeatureToggle.STRICT_MODE.enabled) {
                        ExceptionDialogFragment.newInstance(exception, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    } else {
                        exception.printStackTrace();
                    }

                    return;
                }

                startActivity(EditRowActivity.createIntent(requireContext(), account, fullTable.table(), row.getRow()));
            }

            @Override
            public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
                if (!fullTable.table().hasUpdatePermission() && !fullTable.table().hasDeletePermission()) {
                    Log.i(TAG, "Insufficient permissions: " + EPermissionV2Dto.UPDATE + ", " + EPermissionV2Dto.DELETE);
                    return;
                }

                final var popup = new PopupMenu(requireContext(), cellView.itemView);
                popup.inflate(R.menu.context_menu_cell);
                Optional.ofNullable(popup.getMenu().findItem(R.id.quick_action))
                        .ifPresent(quickActionMenuItem -> {
                            if (cellView instanceof CellViewHolder) {
                                ((CellViewHolder) cellView).getQuickActionProvider().ifPresentOrElse(
                                        quickActionProvider -> {
                                            quickActionMenuItem.setVisible(true);
                                            quickActionMenuItem.setTitle(quickActionProvider.title());
                                        },
                                        () -> quickActionMenuItem.setVisible(false)
                                );
                            } else {
                                quickActionMenuItem.setVisible(false);
                            }
                        });
                popup.setOnMenuItemClickListener(item -> {
                    final var row = adapter.getRowHeaderItem(rowPosition);
                    if (row == null) {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("No row header at position " + rowPosition), account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return false;
                    }

                    if (item.getItemId() == R.id.edit_row) {
                        startActivity(EditRowActivity.createIntent(requireContext(), account, fullTable.table(), row.getRow()));
                    } else if (item.getItemId() == R.id.delete_row) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.delete_row)
                                .setMessage(R.string.delete_row_message)
                                .setPositiveButton(R.string.simple_delete, (dialog, which) -> {
                                    viewTableViewModel.deleteRow(fullTable.table(), row.getRow()).whenCompleteAsync((result, exception) -> {
                                        if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                                            ExceptionDialogFragment.newInstance(exception, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                        }
                                    }, ContextCompat.getMainExecutor(requireContext()));
                                })
                                .setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("Unexpected menu item ID in row context menu: " + item.getItemId()), account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return false;
                    }

                    return true;
                });
                popup.show();
            }

            @Override
            public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
                final var fullColumn = adapter.getColumnHeaderItem(columnPosition);

                if (fullColumn == null) {
                    ExceptionDialogFragment.newInstance(new IllegalStateException("No " + FullColumn.class.getSimpleName() + " header at position " + columnPosition), account)
                            .show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    return;
                }

                final var column = fullColumn.getColumn();
                if (column == null) {
                    ExceptionDialogFragment.newInstance(new IllegalStateException("No " + Column.class.getSimpleName() + " header at position " + columnPosition), account)
                            .show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    return;
                }

                if (!fullTable.table().hasManagePermission()) {
                    Log.i(TAG, "Insufficient permissions: " + EPermissionV2Dto.MANAGE);
                    return;
                }

                final var popup = new PopupMenu(requireContext(), columnHeaderView.itemView);
                popup.inflate(R.menu.context_menu_column);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.edit_column) {
                        if (FeatureToggle.EDIT_COLUMN.enabled) {
                            startActivity(EditColumnActivity.createIntent(requireContext(), account, fullTable.table(), fullColumn));
                        } else {
                            Toast.makeText(requireContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                        }

                    } else if (item.getItemId() == R.id.delete_column) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.delete_item, column.getTitle()))
                                .setMessage(getString(R.string.delete_item_message, column.getTitle()))
                                .setPositiveButton(R.string.simple_delete, (dialog, which) -> {
                                    if (FeatureToggle.DELETE_COLUMN.enabled) {
                                        viewTableViewModel.deleteColumn(fullTable.table(), column).whenCompleteAsync((result, exception) -> {
                                            if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                                                ExceptionDialogFragment.newInstance(exception, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                            }
                                        }, ContextCompat.getMainExecutor(requireContext()));
                                    } else {
                                        Toast.makeText(requireContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("Unexpected menu item ID in column context menu: " + item.getItemId()), account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return false;
                    }

                    return true;
                });
                popup.show();
            }
        });

        binding.fab.setOnClickListener(v -> startActivity(EditRowActivity.createIntent(requireContext(), account, fullTable.table())));
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewTableViewModel.synchronize(account)
                .whenCompleteAsync((result, exception) -> {
                    if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                        ExceptionDialogFragment.newInstance(exception, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    }
                }, ContextCompat.getMainExecutor(requireContext())));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}