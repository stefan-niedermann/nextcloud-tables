package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;

public class NextcloudVersion extends Version {

    private static final Version V_25_0_0 = new Version("25.0.0", 25, 0, 0);

    public NextcloudVersion(@NonNull String version, int major, int minor, int patch) {
        super(version, major, minor, patch);
    }

    public static NextcloudVersion parse(@NonNull String version) {
        return of(Version.parse(version));
    }

    public static NextcloudVersion of(@NonNull Version version) {
        return new NextcloudVersion(version.getVersion(), version.getMajor(), version.getMinor(), version.getPatch());
    }

    public boolean isSupported() {
        return isGreaterThanOrEqual(V_25_0_0);
    }
}
