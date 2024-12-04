package it.niedermann.nextcloud.tables.remote.ocs;


import com.nextcloud.android.sso.model.ocs.OcsResponse;
import com.nextcloud.android.sso.model.ocs.OcsUser;

import it.niedermann.nextcloud.tables.remote.ocs.model.CapabilitiesResponseDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * @link <a href="https://www.open-collaboration-services.org/">OCS REST API</a>
 */
public interface OcsAPI {

    @GET("capabilities?format=json")
    Call<OcsResponse<CapabilitiesResponseDto>> getCapabilities(@Header("If-None-Match") String eTag);

    @GET("users/{userId}?format=json")
    Call<OcsResponse<OcsUser>> getUser(@Path("userId") String userId);
}
