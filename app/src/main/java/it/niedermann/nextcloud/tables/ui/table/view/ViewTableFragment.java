package it.niedermann.nextcloud.tables.ui.table.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
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

        viewTableViewModel.getRows(table).observe(getViewLifecycleOwner(), rows -> {
            adapter.setRowHeaderItems(rows);
        });

        viewTableViewModel.getColumns(table).observe(getViewLifecycleOwner(), columns -> {
            adapter.setColumnHeaderItems(columns);
        });

        Transformations.switchMap(viewTableViewModel.getColumns(table), columns -> viewTableViewModel
                        .getData(table, columns.stream()
                                .map(AbstractRemoteEntity::getRemoteId)
                                .collect(Collectors.toUnmodifiableList())))
                .observe(getViewLifecycleOwner(), data -> {
//                adapter.setCellItems(data);
                });

        binding.fab.setOnClickListener(v -> startActivity(EditRowActivity.createIntent(requireContext(), account)));
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