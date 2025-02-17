package it.niedermann.nextcloud.tables.features.table.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.databinding.FragmentTableBinding;
import it.niedermann.nextcloud.tables.features.column.edit.EditColumnActivity;
import it.niedermann.nextcloud.tables.features.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.features.main.FilterViewModel;
import it.niedermann.nextcloud.tables.features.row.EditRowActivity;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.types.DataTypeViewerServiceRegistry;
import it.niedermann.nextcloud.tables.features.table.view.types.ViewHolderFactory;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EPermissionV2Dto;
import it.niedermann.nextcloud.tables.repository.defaults.DataTypeDefaultServiceRegistry;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public class ViewTableFragment extends Fragment {

    private static final String TAG = ViewTableFragment.class.getSimpleName();
    private FragmentTableBinding binding;
    private ViewTableViewModel viewTableViewModel;
    private FilterViewModel filterViewModel;
    private TableViewAdapter adapter;
    private DataTypeServiceRegistry<ViewHolderFactory> registry;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registry = new DataTypeViewerServiceRegistry(new DataTypeDefaultServiceRegistry());
        binding = FragmentTableBinding.inflate(inflater, container, false);

        viewTableViewModel = new ViewModelProvider(this).get(ViewTableViewModel.class);
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        filterViewModel.getFilterConstraints().observe(getViewLifecycleOwner(), viewTableViewModel::setFilterConstraints);

        adapter = new TableViewAdapter(registry);
        binding.tableView.setAdapter(adapter);
        binding.tableView.getCellRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final var first = binding.tableView.getRowHeaderLayoutManager().findFirstVisibleItemPosition();
                final var last = binding.tableView.getRowHeaderLayoutManager().findLastVisibleItemPosition();
                final var requestedPositionRange = new Range<>((long) first, (long) last);
                viewTableViewModel.requestRowPositionRange(requestedPositionRange);
            }
        });

        viewTableViewModel.getUiState().observe(getViewLifecycleOwner(), this::applyUiState);

        return binding.getRoot();
    }

    private void applyUiState(@NonNull ViewTableViewModel.UiState state) {
        binding.tableView.getScrollHandler().scrollToRowPosition(0);
        binding.tableView.getScrollHandler().scrollToColumnPosition(0);

        final var fullTable = state.currentFullTable();

        if (fullTable == null) {
            Log.i(TAG, "Current table: " + null);
            adapter.setAllItems(state.account(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 0);
            binding.tableView.setTableViewListener(null);
            return;
        }

        Log.i(TAG, "Current table: " + fullTable.getTable());

        final var rowPosition = binding.tableView.getCellLayoutManager().findFirstVisibleItemPosition();
        final var columnPosition = binding.tableView.getColumnHeaderLayoutManager().findFirstVisibleItemPosition();

        // Workaround for https://github.com/stefan-niedermann/nextcloud-tables/issues/16
        if (fullTable.getRows().isEmpty()) {
            adapter.setAllItems(state.account(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 0);
        } else {
            adapter.setAllItems(state.account(), fullTable.getColumns(), fullTable.getRows(), state.dataGrid(), state.currentFullTable().getRowCount());
        }

        binding.tableView.getCellLayoutManager().scrollToPosition(rowPosition);
        binding.tableView.getRowHeaderLayoutManager().scrollToPosition(rowPosition);
        binding.tableView.getColumnHeaderLayoutManager().scrollToPosition(columnPosition);

        binding.tableView.setTableViewListener(new DefaultTableViewListener() {
            @Override
            public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
                if (!fullTable.getTable().hasUpdatePermission()) {
                    Log.i(TAG, "Insufficient permissions: " + EPermissionV2Dto.UPDATE);
                    return;
                }

                if (rowPosition >= fullTable.getRows().size()) {
                    final var exception = new IllegalStateException("Tried to access rowPosition " + rowPosition + " but there were only " + fullTable.getRows().size() + " rows.");

                    if (FeatureToggle.STRICT_MODE.enabled) {
                        ExceptionDialogFragment.newInstance(exception, state.account()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    } else {
                        exception.printStackTrace();
                    }

                    return;
                }

                final var row = fullTable.getRows().get(rowPosition);
                if (row == null) {
                    final var exception = new IllegalStateException("No row header at position " + rowPosition);

                    if (FeatureToggle.STRICT_MODE.enabled) {
                        ExceptionDialogFragment.newInstance(exception, state.account()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    } else {
                        exception.printStackTrace();
                    }

                    return;
                }

                startActivity(EditRowActivity.createEditIntent(requireContext(), state.account(), fullTable.getTable(), row.getRow()));
            }

            @Override
            public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
                if (!fullTable.getTable().hasUpdatePermission() && !fullTable.getTable().hasDeletePermission()) {
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
                        ExceptionDialogFragment.newInstance(new IllegalStateException("No row header at position " + rowPosition), state.account()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return false;
                    }

                    if (item.getItemId() == R.id.edit_row) {
                        startActivity(EditRowActivity.createEditIntent(requireContext(), state.account(), fullTable.getTable(), row.getRow()));
                    } else if (item.getItemId() == R.id.duplicate_row) {
                        startActivity(EditRowActivity.createDuplicateIntent(requireContext(), state.account(), fullTable.getTable(), row.getRow()));
                    } else if (item.getItemId() == R.id.delete_row) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.delete_row)
                                .setMessage(R.string.delete_row_message)
                                .setPositiveButton(R.string.simple_delete, (dialog, which) -> {
                                    viewTableViewModel.deleteRow(fullTable.getTable(), row.getRow()).whenCompleteAsync((result, exception) -> {
                                        if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                                            ExceptionDialogFragment.newInstance(exception, state.account()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                        }
                                    }, ContextCompat.getMainExecutor(requireContext()));
                                })
                                .setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("Unexpected menu item ID in row context menu: " + item.getItemId()), state.account()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return false;
                    }

                    return true;
                });
                popup.show();
            }

            @Override
            public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
                final var column = adapter.getColumnHeaderItem(columnPosition);

                if (column == null) {
                    ExceptionDialogFragment.newInstance(new IllegalStateException("No " + Column.class.getSimpleName() + " header at position " + columnPosition), state.account())
                            .show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    return;
                }

                if (!fullTable.getTable().hasManagePermission()) {
                    Log.i(TAG, "Insufficient permissions: " + EPermissionV2Dto.MANAGE);
                    return;
                }

                final var popup = new PopupMenu(requireContext(), columnHeaderView.itemView);
                popup.inflate(R.menu.context_menu_column);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.edit_column) {
                        if (FeatureToggle.EDIT_COLUMN.enabled) {
                            startActivity(EditColumnActivity.createIntent(requireContext(), state.account(), fullTable.getTable(), column));
                        } else {
                            Toast.makeText(requireContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                        }

                    } else if (item.getItemId() == R.id.delete_column) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.delete_item, column.getColumn().getTitle()))
                                .setMessage(getString(R.string.delete_item_message, column.getColumn().getTitle()))
                                .setPositiveButton(R.string.simple_delete, (dialog, which) -> {
                                    if (FeatureToggle.DELETE_COLUMN.enabled) {
                                        viewTableViewModel.deleteColumn(fullTable.getTable(), column.getColumn()).whenCompleteAsync((result, exception) -> {
                                            if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                                                ExceptionDialogFragment.newInstance(exception, state.account()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                            }
                                        }, ContextCompat.getMainExecutor(requireContext()));
                                    } else {
                                        Toast.makeText(requireContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("Unexpected menu item ID in column context menu: " + item.getItemId()), state.account()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return false;
                    }

                    return true;
                });
                popup.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}