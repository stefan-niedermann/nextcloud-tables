package it.niedermann.nextcloud.tables.remote.ocs.model;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

import java.io.Serializable;

/**
 * Utilizing {@link OcsCapabilitiesResponse} classes combined with own tables specific information
 */
public record CapabilitiesResponse(
        OcsCapabilitiesResponse.OcsVersion version,
        OcsCapabilities capabilities
) implements Serializable {
    public record OcsCapabilities(
            OcsCapabilitiesResponse.OcsCapabilities.OcsTheming theming,
            Tables tables
    ) implements Serializable {
        public record Tables(
                boolean enabled,
                String version
        ) implements Serializable {
        }
    }
}
