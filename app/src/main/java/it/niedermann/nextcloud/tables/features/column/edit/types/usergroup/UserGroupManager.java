package it.niedermann.nextcloud.tables.features.column.edit.types.usergroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.attributes.UserGroupAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageUsergroupBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public class UserGroupManager extends ColumnEditView<ManageUsergroupBinding> {


    private final Map<CompoundButton, MutableLiveData<Boolean>> checkboxGroup = new HashMap<>();

    public final MutableLiveData<Boolean> enableUsers = new ReactiveLiveData<>(true);
    public final MutableLiveData<Boolean> enableGroups = new ReactiveLiveData<>(false);
    public final MutableLiveData<Boolean> enableTeams = new ReactiveLiveData<>(false);

    private final MutableLiveData<Boolean> usersChecked = new ReactiveLiveData<>(true);
    public final LiveData<Integer> showUserStatusVisibility = new ReactiveLiveData<>(usersChecked)
            .map(usersAllowed -> usersAllowed ? View.VISIBLE : View.GONE);

    public UserGroupManager(@NonNull Context context) {
        super(context);
    }

    public UserGroupManager(@NonNull Context context,
                            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UserGroupManager(@NonNull Context context,
                            @Nullable FragmentManager fragmentManager) {
        super(context, DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.manage_usergroup, null, false), fragmentManager);

        binding.setLifecycleOwner(this);
        binding.setManager(this);

        checkboxGroup.put(binding.users, enableUsers);
        checkboxGroup.put(binding.groups, enableGroups);
        checkboxGroup.put(binding.teams, enableTeams);
    }

    public void ensureAtLeastOneTypeEnabled() {
        usersChecked.setValue(binding.users.isChecked());

        final var checkedTypes = checkboxGroup
                .keySet()
                .stream()
                .map(CompoundButton::isChecked)
                .filter(Boolean.TRUE::equals)
                .count();

        if (checkedTypes > 1) {

            checkboxGroup
                    .values()
                    .forEach(liveData -> liveData.setValue(true));

        } else if (checkedTypes == 1) {

            checkboxGroup
                    .keySet()
                    .stream()
                    .filter(CompoundButton::isChecked)
                    .findAny()
                    .map(checkboxGroup::get)
                    .stream()
                    .findAny()
                    .ifPresent(liveData -> liveData.setValue(false));

        } else if (FeatureToggle.STRICT_MODE.enabled) {

            ExceptionDialogFragment.newInstance(new IllegalStateException("All checkboxes are disabled. Expected the last checked one to be disabled."), null).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName());

        } else {

            checkboxGroup
                    .values()
                    .forEach(liveData -> liveData.setValue(true));

        }
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        fullColumn.getColumn().setUserGroupAttributes(new UserGroupAttributes(
                binding.selectMultipleItems.isChecked(),
                binding.users.isChecked(),
                binding.groups.isChecked(),
                binding.teams.isChecked(),
                binding.users.isChecked() && binding.showUserStatus.isChecked()
        ));

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        binding.users.setChecked(!isCreateMode() || fullColumn.getColumn().getUserGroupAttributes().usergroupSelectUsers());
        binding.groups.setChecked(fullColumn.getColumn().getUserGroupAttributes().usergroupSelectGroups());
        binding.teams.setChecked(fullColumn.getColumn().getUserGroupAttributes().usergroupSelectTeams());
        binding.selectMultipleItems.setChecked(fullColumn.getColumn().getUserGroupAttributes().usergroupMultipleItems());
        binding.showUserStatus.setChecked(fullColumn.getColumn().getUserGroupAttributes().showUserStatus());

        ensureAtLeastOneTypeEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        checkboxGroup.keySet().forEach(checkbox -> checkbox.setEnabled(enabled));
        binding.selectMultipleItems.setEnabled(enabled);
        binding.showUserStatus.setEnabled(enabled);

        if (enabled) { // TODO Abfrage in ensureAtLeastOneTypeEnabled Methode
            ensureAtLeastOneTypeEnabled();
        }
    }
}
