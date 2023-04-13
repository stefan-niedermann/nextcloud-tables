package it.niedermann.nextcloud.tables.model;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

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

    public static NextcloudVersion of(@NonNull OcsCapabilitiesResponse.OcsVersion version) {
        return new NextcloudVersion(version.string, version.major, version.minor, version.macro);
    }

    public boolean isSupported() {
        return isGreaterThanOrEqual(V_25_0_0);
    }
}
