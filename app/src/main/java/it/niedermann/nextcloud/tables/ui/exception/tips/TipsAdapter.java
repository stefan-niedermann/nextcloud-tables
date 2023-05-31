package it.niedermann.nextcloud.tables.ui.exception.tips;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment.INTENT_EXTRA_BUTTON_TEXT;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.sso.exceptions.NextcloudApiNotRespondingException;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotSupportedException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.exceptions.TokenMismatchException;
import com.nextcloud.android.sso.exceptions.UnknownErrorException;
import com.nextcloud.android.sso.model.FilesAppType;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.exception.InsufficientPermissionException;
import it.niedermann.nextcloud.tables.remote.exception.ServerNotAvailableException;

public class TipsAdapter extends RecyclerView.Adapter<TipsViewHolder> {

    private static final Intent INTENT_APP_INFO = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
            .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_app_info);

    @NonNull
    private final Consumer<Intent> actionButtonClickedListener;
    @NonNull
    private final List<TipsModel> tips = new LinkedList<>();

    public TipsAdapter(@NonNull Consumer<Intent> actionButtonClickedListener) {
        this.actionButtonClickedListener = actionButtonClickedListener;
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final var view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tip, parent, false);
        return new TipsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
        holder.bind(tips.get(position), actionButtonClickedListener);
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    public void setThrowable(@NonNull Context context, @Nullable Account account, @NonNull Throwable rawThrowable) {
        final var throwable = unwrapThrowable(rawThrowable);

        if (throwable instanceof ServerNotAvailableException) {
            add(((ServerNotAvailableException) throwable).getReason().messageRes);

            switch (((ServerNotAvailableException) throwable).getReason()) {
                case NOT_INSTALLED:
                case NOT_ENABLED:
                case MAINTENANCE_MODE:
                case SERVER_ERROR:
                case DEVICE_OFFLINE:
                case TABLES_NOT_SUPPORTED:
                case NEXTCLOUD_NOT_SUPPORTED:
                    break;
                case UNKNOWN:
                default:
                    add(R.string.error_dialog_tip_clear_storage_might_help);
                    add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
                    break;
            }
        } else if (throwable instanceof InsufficientPermissionException) {
            switch (((InsufficientPermissionException) throwable).getMissingPermission()) {
                case READ: {
                    add(R.string.missing_permission_read);
                    break;
                }
                case CREATE: {
                    add(R.string.missing_permission_create);
                    break;
                }
                case UPDATE: {
                    add(R.string.missing_permission_update);
                    break;
                }
                case DELETE: {
                    add(R.string.missing_permission_delete);
                    break;
                }
                case MANAGE: {
                    add(R.string.missing_permission_manage);
                    break;
                }
                default:
                    add(R.string.reason_unknown);
                    add(R.string.error_dialog_tip_clear_storage_might_help);
                    add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
            }
        } else if (throwable instanceof TokenMismatchException) {
            add(R.string.error_dialog_tip_token_mismatch_retry);
            add(R.string.error_dialog_tip_clear_storage_might_help);
            add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
        } else if (throwable instanceof NextcloudFilesAppAccountNotFoundException) {
            // TODO we can give better hints here...
            add(R.string.error_dialog_tip_token_mismatch_retry);
            add(R.string.error_dialog_tip_clear_storage_might_help);
            add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
        } else if (throwable instanceof NextcloudFilesAppNotSupportedException) {
            add(R.string.error_dialog_min_version, new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nextcloud.client"))
                    .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_update_files_app));
        } else if (throwable instanceof NextcloudApiNotRespondingException) {
            add(R.string.error_dialog_tip_disable_battery_optimizations, new Intent().setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_battery_settings));
            add(R.string.error_dialog_tip_files_force_stop);
            add(R.string.error_dialog_tip_files_delete_storage);
        } else if (throwable instanceof SocketTimeoutException || throwable instanceof ConnectException) {
            add(R.string.error_dialog_timeout_instance);
            add(R.string.error_dialog_timeout_toggle, new Intent(Settings.ACTION_WIFI_SETTINGS).putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_network));
        } else if (throwable instanceof JSONException || throwable instanceof NullPointerException) {
            add(R.string.error_dialog_check_server);
        } else if (throwable instanceof NextcloudHttpRequestFailedException) {
            int statusCode = ((NextcloudHttpRequestFailedException) throwable).getStatusCode();
            switch (statusCode) {
                case 302:
                    add(R.string.error_dialog_redirect);
                    break;
                case 403:
                    add(R.string.error_dialog_forbidden);
                    break;
                case 500:
                    if (account != null) {
                        add(R.string.error_dialog_check_server_logs, new Intent(Intent.ACTION_VIEW)
                                .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_server_logs)
                                .setData(Uri.parse(account.getUrl() + context.getString(R.string.url_fragment_server_logs))));
                    } else {
                        add(R.string.error_dialog_check_server_logs);
                    }
                    break;
                case 503:
                    add(R.string.error_dialog_check_maintenance);
                    break;
                case 507:
                    add(R.string.error_dialog_insufficient_storage);
                    break;
            }
        } else if (throwable instanceof ClassNotFoundException) {
            final Throwable cause = ((ClassNotFoundException) throwable).getCause();
            if (cause != null) {
                final String message = cause.getMessage();
                if (message != null && message.toLowerCase().contains("certificate")) {
                    final Intent filesOpenIntent = getOpenFilesIntent(context);
                    if (filesOpenIntent == null) {
                        add(R.string.error_dialog_certificate);
                    } else {
                        add(R.string.error_dialog_certificate, filesOpenIntent);
                    }
                }
            }
        } else if (throwable instanceof RuntimeException) {
            if (throwable.getMessage() != null && throwable.getMessage().toLowerCase().contains("database")) {
                final var intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_report_bug)))
                        .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_report_issue);
                add(R.string.error_dialog_tip_database_upgrade_failed, intent);
                add(R.string.error_dialog_tip_clear_storage, INTENT_APP_INFO);
            }
        } else if (throwable instanceof UnknownErrorException) {
            if ("com.nextcloud.android.sso.QueryParam".equals(throwable.getMessage())) {
                add(R.string.error_dialog_min_version, new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nextcloud.client"))
                        .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_update_files_app));
            } else {
                if (account != null) {
                    add(R.string.error_dialog_unknown_error, new Intent(Intent.ACTION_VIEW)
                            .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_in_browser)
                            .setData(Uri.parse(account.getUrl())));
                } else {
                    add(R.string.error_dialog_unknown_error);
                }
            }
        }
    }

    @NonNull
    private Throwable unwrapThrowable(@NonNull Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            final var cause = throwable.getCause();
            if (cause == null) {
                return throwable;
            } else {
                return unwrapThrowable(cause);
            }
        } else {
            return throwable;
        }
    }

    public void add(@StringRes int text) {
        add(text, null);
    }

    public void add(@StringRes int text, @Nullable Intent primaryAction) {
        tips.add(new TipsModel(text, primaryAction));
        notifyItemInserted(tips.size());
    }

    @Nullable
    private static Intent getOpenFilesIntent(@NonNull Context context) {
        final var pm = context.getPackageManager();
        for (final var filesAppType : FilesAppType.values()) {
            try {
                pm.getPackageInfo(filesAppType.packageId, PackageManager.GET_ACTIVITIES);
                return pm.getLaunchIntentForPackage(filesAppType.packageId)
                        .putExtra(INTENT_EXTRA_BUTTON_TEXT, R.string.error_action_open_nextcloud_app);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return null;
    }
}