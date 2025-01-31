package it.niedermann.nextcloud.tables.remote.ocs;


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.model.ocs.OcsResponse;
import com.nextcloud.android.sso.model.ocs.OcsUser;

import java.util.List;

import it.niedermann.nextcloud.tables.remote.ocs.model.CapabilitiesResponse;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchProvider;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @link <a href="https://www.open-collaboration-services.org/">OCS REST API</a>
 */
public interface OcsAPI {

    @GET("cloud/capabilities?format=json")
    Call<OcsResponse<CapabilitiesResponse>> getCapabilities(@Header("If-None-Match") @Nullable String eTag);

    @GET("cloud/users/{userId}?format=json")
    Call<OcsResponse<OcsUser>> getUser(@Header("If-None-Match") @Nullable String eTag,
                                       @Path("userId") @NonNull String userId);


    @GET("core/autocomplete/get?format=json")
    Call<OcsResponse<List<OcsAutocompleteResult>>> searchUser(@Header("If-None-Match") @Nullable String eTag,
                                                              @Query("search") @NonNull String term,
                                                              /// `0` = user, `1` = group, should match OcsAutocompleteSource#shareType
                                                              /// TODO User converter class and replace int with enum
                                                              @Query("shareTypes[]") @NonNull List<Integer> shareTypes,
                                                              @Query("itemType") @Nullable List<Integer> itemType,
                                                              @Query("itemId") @Nullable Long itemId,
                                                              @Query("limit") int limit);

    @GET("search/providers?format=json")
    Call<OcsResponse<List<OcsSearchProvider>>> getSearchProviders();

    @GET("search/providers/{provider}/search?format=json")
    Call<OcsResponse<OcsSearchResult>> search(@Header("If-None-Match") @Nullable String eTag,
                                              @Path("provider") @NonNull String provider,
                                              @Query("term") @NonNull String term);

    /// @return ```json
    /// "references": {
    ///   "https:\/\/dev2.cloud.niedermann.it\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf": {
    ///       "richObjectType": "open-graph",
    ///               "richObject": {
    ///           "id": "https:\/\/dev2.cloud.niedermann.it\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "name": "https:\/\/dev2.cloud.niedermann.it\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "description": null,
    ///                   "thumb": null,
    ///                   "link": "https:\/\/dev2.cloud.niedermann.it\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf"
    ///       },
    ///       "openGraphObject": {
    ///           "id": "https:\/\/dev2.cloud.niedermann.it\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "name": "https:\/\/dev2.cloud.niedermann.it\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "description": null,
    ///                   "thumb": null,
    ///                   "link": "https:\/\/dev2.cloud.niedermann.it\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf"
    ///       },
    ///       "accessible": true
    ///   }
    /// }
    /// ```
    @GET("references/resolve")
    Call<OcsResponse<Object>> resolve(@Header("If-None-Match") @Nullable String eTag,
                                      @Query("reference") @NonNull Uri reference);
}
