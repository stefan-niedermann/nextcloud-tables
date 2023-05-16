package it.niedermann.nextcloud.tables.model;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

public class TablesVersion extends Version {

    private static final Version V_0_5_0 = new Version("0.5.0", 0, 5, 0);

    public TablesVersion(@NonNull String version, int major, int minor, int patch) {
        super(version, major, minor, patch);
    }

    public static TablesVersion parse(@NonNull String version) {
        return of(Version.parse(version));
    }

    public static TablesVersion of(@NonNull Version version) {
        return new TablesVersion(version.getVersion(), version.getMajor(), version.getMinor(), version.getPatch());
    }

    public static TablesVersion of(@NonNull OcsCapabilitiesResponse.OcsVersion version) {
        return new TablesVersion(version.string, version.major, version.minor, version.macro);
    }

    public boolean isSupported() {
        return isGreaterThanOrEqual(V_0_5_0);
    }
}
