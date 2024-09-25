package it.niedermann.nextcloud.tables.remote.model;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

/**
 * Utilizing {@link OcsCapabilitiesResponse} classes combined with own tables specific information
 */
public class CapabilitiesResponse {
    public OcsCapabilitiesResponse.OcsVersion version;
    public OcsCapabilities capabilities;

    public static class OcsCapabilities {
        public OcsCapabilitiesResponse.OcsCapabilities.OcsTheming theming;
        public Tables tables;

        public static class Tables {
            public boolean enabled;
            public String version;
        }
    }
}
