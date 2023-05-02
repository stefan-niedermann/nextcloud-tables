package it.niedermann.nextcloud.tables.ui.table.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.listener.ITableViewListener;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.FragmentTableBinding;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.row.EditRowActivity;

public class ViewTableFragment extends Fragment {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private FragmentTableBinding binding;
    private ViewTableViewModel viewTableViewModel;
    private Account account;
    private Table table;
    private TableViewAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        final var args = getArguments();
        if (args == null || !args.containsKey(KEY_ACCOUNT) || !args.containsKey(KEY_TABLE)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_TABLE + " must be provided.");
        }

        table = (Table) args.getSerializable(KEY_TABLE);
        account = (Account) args.getSerializable(KEY_ACCOUNT);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewTableViewModel = new ViewModelProvider(this).get(ViewTableViewModel.class);
        binding = FragmentTableBinding.inflate(inflater, container, false);

        adapter = new TableViewAdapter();
        binding.tableView.setAdapter(adapter);

        viewTableViewModel.getFullTable(table).observe(getViewLifecycleOwner(), data -> {
            adapter.setAllItems(data.getColumns(), data.getRows(), data.getData());
            binding.tableView.setTableViewListener(new ITableViewListener() {
                @Override
                public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
                    final var row = adapter.getRowHeaderItem(rowPosition);
                    if (row == null) {
                        ExceptionDialogFragment.newInstance(new IllegalStateException("No row header at position " + rowPosition), account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    } else {
                        startActivity(EditRowActivity.createIntent(requireContext(), account, table, row));
                    }
                }

                @Override
                public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

                }

                @Override
                public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

                }

                @Override
                public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

                }

                @Override
                public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

                }

                @Override
                public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

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
        });

        binding.fab.setOnClickListener(v -> startActivity(EditRowActivity.createIntent(requireContext(), account, table)));
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewTableViewModel.synchronizeAccountAndTables(account).whenCompleteAsync((result, exception) -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (exception != null) {
                ExceptionDialogFragment.newInstance(exception, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        }, ContextCompat.getMainExecutor(requireContext())));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static ViewTableFragment newInstance(@NonNull Account account, @NonNull Table table) {
        final var fragment = new ViewTableFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putSerializable(KEY_TABLE, table);
        fragment.setArguments(args);

        return fragment;
    }
}