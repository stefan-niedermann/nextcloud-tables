package it.niedermann.nextcloud.tables.shared.config;

import it.niedermann.nextcloud.tables.shared.BuildConfig;

public enum FeatureToggle {

    /// Some exceptions only affect a part of the app. Enabling [#STRICT_MODE] will always
    /// throw all exceptions to make the user aware of the fact that something went wrong.
    /// Disabling this [FeatureToggle] can lead to wrongly displayed data.
    STRICT_MODE(BuildConfig.DEBUG),
    EDIT_COLUMN(BuildConfig.DEBUG),
    CREATE_COLUMN(BuildConfig.DEBUG),
    DELETE_COLUMN(BuildConfig.DEBUG),
    SHARE_TABLE(BuildConfig.DEBUG),
    SEARCH_IN_TABLE(false),
    ;

    public final boolean enabled;

    FeatureToggle(boolean enabled) {
        this.enabled = enabled;
    }
}