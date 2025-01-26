package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;

public class TablesVersion extends Version {

    public static final TablesVersion V_0_5_0 = new TablesVersion("0.5.0", 0, 5, 0);
    public static final TablesVersion V_0_8_0 = new TablesVersion("0.8.0", 0, 8, 0);

    public TablesVersion(@NonNull String version, int major, int minor, int patch) {
        super(version, major, minor, patch);
    }

    public static TablesVersion parse(@NonNull String version) {
        return of(Version.parse(version));
    }

    public static TablesVersion of(@NonNull Version version) {
        return new TablesVersion(version.getVersion(), version.getMajor(), version.getMinor(), version.getPatch());
    }

    public boolean isSupported() {
        return isGreaterThanOrEqual(V_0_8_0);
    }
}
