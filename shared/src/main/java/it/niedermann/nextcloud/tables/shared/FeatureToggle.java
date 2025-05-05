package it.niedermann.nextcloud.tables.shared;

public enum FeatureToggle {

    /// Some exceptions only affect a part of the app. Enabling [#STRICT_MODE] will always
    /// throw all exceptions to make the user aware of the fact that something went wrong.
    /// Disabling this [FeatureToggle] can lead to wrongly displayed data.
    STRICT_MODE(BuildConfig.DEBUG),
    EDIT_COLUMN(true),
    EDIT_USER_GROUPS(BuildConfig.DEBUG),
    CREATE_COLUMN(true),
    DELETE_COLUMN(true),
    SHARE_TABLE(BuildConfig.DEBUG),
    SEARCH_IN_TABLE(false),
    ;

    public final boolean enabled;

    FeatureToggle(boolean enabled) {
        this.enabled = enabled;
    }
}