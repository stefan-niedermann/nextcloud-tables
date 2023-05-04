package it.niedermann.nextcloud.tables.ui.table.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.listener.ITableViewListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Collections;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.FragmentTableBinding;
import it.niedermann.nextcloud.tables.model.FullTable;
import it.niedermann.nextcloud.tables.ui.column.edit.EditColumnActivity;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.row.EditRowActivity;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class ViewTableFragment extends Fragment {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private FragmentTableBinding binding;
    private ViewTableViewModel viewTableViewModel;
    private TableViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTableBinding.inflate(inflater, container, false);
        adapter = new TableViewAdapter();
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
        viewTableViewModel.getCurrentFullTable().observe(getViewLifecycleOwner(), pair -> applyCurrentTable(pair.first, pair.second));
    }

    private void applyCurrentTable(@NonNull Account account, @Nullable FullTable fullTable) {
        if (fullTable == null) {
            adapter.setAllItems(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            binding.tableView.setTableViewListener(null);
            binding.fab.setOnClickListener(null);
            binding.swipeRefreshLayout.setOnRefreshListener(null);
        } else {
            adapter.setAllItems(fullTable.getColumns(), fullTable.getRows(), fullTable.getData());
            binding.tableView.setTableViewListener(new ITableViewListener() {
                @Override
                public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
                    final var row = adapter.getRowHeaderItem(rowPosition);
                    if (row == null) {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("No row header at position " + rowPosition), account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    } else {
                        startActivity(EditRowActivity.createIntent(requireContext(), account, fullTable.getTable(), row));
                    }
                }

                @Override
                public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {

                }

                @Override
                public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
                    final var popup = new PopupMenu(requireContext(), cellView.itemView);
                    popup.inflate(R.menu.context_menu_cell);
                    Optional.ofNullable(popup.getMenu().findItem(R.id.quick_action))
                            .ifPresent(quickActionMenuItem -> {
                                if (cellView instanceof CellViewHolder) {
                                    ((CellViewHolder) cellView).getQuickActionProvider().ifPresentOrElse(
                                            quickActionProvider -> {
                                                quickActionMenuItem.setVisible(true);
                                                quickActionMenuItem.setTitle(quickActionProvider.getTitle());
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
                            startActivity(EditRowActivity.createIntent(requireContext(), account, fullTable.getTable(), row));
                        } else if (item.getItemId() == R.id.delete_row) {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(R.string.delete_row)
                                    .setMessage(R.string.delete_row_message)
                                    .setPositiveButton(R.string.simple_delete, (dialog, which) -> viewTableViewModel.deleteRow(row))
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
                public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

                }

                @Override
                public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

                }

                @Override
                public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
                    final var column = adapter.getColumnHeaderItem(columnPosition);
                    if (column == null) {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("No column header at position " + columnPosition), account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return;
                    }

                    final var popup = new PopupMenu(requireContext(), columnHeaderView.itemView);
                    popup.inflate(R.menu.context_menu_column);
                    Optional.ofNullable(popup.getMenu().findItem(R.id.delete_column))
                            .ifPresent(item -> item.setTitle(getString(R.string.delete_item, column.getTitle())));
                    popup.setOnMenuItemClickListener(item -> {

                        if (item.getItemId() == R.id.edit_column) {
                            startActivity(EditColumnActivity.createIntent(requireContext(), account, fullTable.getTable(), column));

                        } else if (item.getItemId() == R.id.delete_column) {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(getString(R.string.delete_item, column.getTitle()))
                                    .setMessage(getString(R.string.delete_item_message, column.getTitle()))
                                    .setPositiveButton(R.string.simple_delete, (dialog, which) -> viewTableViewModel.deleteColumn(column))
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

                @Override
                public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

                }

                @Override
                public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

                }

                @Override
                public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

                }
            });

            binding.fab.setOnClickListener(v -> startActivity(EditRowActivity.createIntent(requireContext(), account, fullTable.getTable())));
            binding.swipeRefreshLayout.setOnRefreshListener(() -> viewTableViewModel.synchronizeAccountAndTables(account).whenCompleteAsync((result, exception) -> {
                // TODO fragment gets detached by MainActivity, this code will fail.
                binding.swipeRefreshLayout.setRefreshing(false);
                if (exception != null) {
                    ExceptionDialogFragment.newInstance(exception, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }, ContextCompat.getMainExecutor(requireContext())));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}