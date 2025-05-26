package it.niedermann.nextcloud.tables.repository;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API.TEXT_LINK_PROVIDER_ID_URL;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.entity.attributes.UserGroupAttributes;
import it.niedermann.nextcloud.tables.remote.RequestHelper;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResult;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;
import retrofit2.Response;

@MainThread
public class SearchRepository extends AbstractRepository {

    private static final String TAG = SearchRepository.class.getSimpleName();
    private final RequestHelper requestHelper;

    public SearchRepository(@NonNull Context context) {
        super(context);
        this.requestHelper = new RequestHelper(context);
    }

    @NonNull
    public LiveData<List<SearchProvider>> getSearchProvider(long accountId) {
        return db.getSearchProviderDao().getSearchProvider(accountId);
    }

    @NonNull
    public LiveData<List<OcsAutocompleteResult>> searchUser(@NonNull Account account,
                                                            @NonNull UserGroupAttributes userGroupAttributes,
                                                            @NonNull String term) {
        final var liveData = new ReactiveLiveData<List<OcsAutocompleteResult>>(Collections.emptyList());


        // TODO Implement offline cache search
        if (!term.isEmpty()) {
            searchUserOnline(account, userGroupAttributes, term)
                    .thenAcceptAsync(liveData::postValue, workExecutor);
        }

        return liveData;
    }

    @NonNull
    public CompletableFuture<List<OcsAutocompleteResult>> searchUserOnline(@NonNull Account account,
                                                                           @NonNull UserGroupAttributes userGroupAttributes,
                                                                           @NonNull String term) {
        final var sources = new ArrayList<OcsAutocompleteResult.OcsAutocompleteSource>(2);
        if (userGroupAttributes.usergroupSelectUsers()) {
            sources.add(OcsAutocompleteResult.OcsAutocompleteSource.USERS);
        }
        if (userGroupAttributes.usergroupSelectGroups()) {
            sources.add(OcsAutocompleteResult.OcsAutocompleteSource.GROUPS);
        }
        if (userGroupAttributes.usergroupSelectGroups()) {
            sources.add(OcsAutocompleteResult.OcsAutocompleteSource.TEAMS);
        }

        return requestHelper.executeOcsRequest(account, api -> api.searchUser(
                        null, term,
                        sources.stream().mapToInt(st -> st.shareType).boxed().toList(),
                        null, null, 10))
                .handleAsync((response, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                        return Collections.emptyList();
                    }

                    return Optional.ofNullable(response)
                            .map(Response::body)
                            .map(ocsResponse -> ocsResponse.ocs)
                            .filter(ocs -> ocs.meta.statusCode == HttpURLConnection.HTTP_OK)
                            .map(ocs -> ocs.data)
                            .orElseGet(Collections::emptyList);
                }, workExecutor);
    }

    @AnyThread
    @NonNull
    public LiveData<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> search(@NonNull Account account,
                                                                                   @NonNull Collection<String> searchProviderIds,
                                                                                   @NonNull String term) {

        final var urlSearch = Optional.of(searchProviderIds)
                .filter(set -> set.contains(TEXT_LINK_PROVIDER_ID_URL))
                .flatMap(v -> searchUrl(account, term))
                .map(ReactiveLiveData::new)
                .map(liveData -> liveData
                        .map(entry -> new Pair<SearchProvider, OcsSearchResultEntry>(null, entry))
                        .map(Collections::singleton))
                .orElseGet(() -> new ReactiveLiveData<>(emptySet()));

        // TODO Locally search for tables
//        final var tablesSearch = searchProviderIds
//                .filter(set -> set.contains("tables"))
//                .map(v -> searchTables(account, column, term))
//                .orElseGet(MutableLiveData::new);
//
//        final var localSearch = new ReactiveLiveData<>(urlSearch)
//                .combineWith(() -> tablesSearch)
//                .map(args -> new LocalSearchSources(args.first, args.second));

        /// We start with an empty map to not block the local search results
        final var remoteSearchProviderIds = searchProviderIds
                .stream()
                .filter(not(TEXT_LINK_PROVIDER_ID_URL::equals))
                // TODO .filter(not... in tables and other local providers
                .collect(toUnmodifiableSet());

        final var remoteSearch = new ReactiveLiveData<>(Collections.emptyMap())
                .flatMap(() -> searchRemote(account, remoteSearchProviderIds, term));

        return new ReactiveLiveData<>(urlSearch)
                .combineWith(() -> remoteSearch)
                .map(results -> Stream.of(results.first, results.second)
                        .flatMap(Collection::stream)
                        .toList());
    }

    @NonNull
    public Optional<LiveData<OcsSearchResultEntry>> searchUrl(@NonNull Account account,
                                                              @NonNull String term) {
        final var searchResult = new ReactiveLiveData<OcsSearchResultEntry>();

        if (Patterns.DOMAIN_NAME.matcher(term).matches()) {
            searchResult.postValue(createUrlSearchResultEntry(String.format("https://%s", term)));
            return Optional.of(searchResult);

        } else if (Patterns.WEB_URL.matcher(term).matches()) {
            searchResult.postValue(createUrlSearchResultEntry(term));

//        requestHelper.executeNetworkRequest(account, apiTuple -> apiTuple.ocs().resolve(null, Uri.parse(term)))
//                .whenCompleteAsync((result, exception) -> {
//                    if (result != null && exception == null) {
//                         TODO post result as new searchResult value
//                    }
//                });

            return Optional.of(searchResult);

        }

        return Optional.empty();
    }

    @NonNull
    private OcsSearchResultEntry createUrlSearchResultEntry(@NonNull String term) {
        return new OcsSearchResultEntry(null, Uri.parse(term).getHost(), "Url", term, null, false, emptyMap());
    }

//    @NonNull
//    public LiveData<Pair<SearchProvider, Collection<OcsSearchResultEntry>>> searchTables(@NonNull Account account,
//                                                                                         @NonNull Column column,
//                                                                                         @NonNull String term) {
//        supplyAsync(() -> Optional.of(column)
//                .map(Column::getTextAttributes)
//                .map(TextAttributes::textAllowedPattern)
//                .map(pattern -> pattern.split(","))
//                .map(Set::of)
//                .flatMap(set -> set.stream().filter(TablesV1API.TEXT_LINK_PROVIDER_ID_TABLES::equals).findAny())
//                .orElseGet(Collections::emptySet), workExecutor)
//                .thenApplyAsync(allowedSearchProviders -> db.getSearchProviderDao().getSearchProvider(column.getAccountId(), allowedSearchProviders), db.getParallelExecutor())
//                .thenAcceptAsync(searchProviders -> searchProviders

    /// /        db.getSearchProviderDao().getSearchProvider(column.getAccountId(), allowedSearchProviders)
//        return null;
//    }

    // TODO mapping might belong into calling ViewModel
    @NonNull
    public LiveData<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> searchRemote(@NonNull Account account,
                                                                                         @NonNull Collection<String> searchProviderIds,
                                                                                         @NonNull String term) {
        return new ReactiveLiveData<>(searchOnline(account, searchProviderIds, term))
                .map(results -> results.entrySet()
                        .stream()
                        .flatMap(entry -> entry
                                .getValue()
                                .stream()
                                .map(value -> new Pair<>(entry.getKey(), value)))
                        .toList());
    }

    @NonNull
    private LiveData<Map<SearchProvider, Collection<OcsSearchResultEntry>>> searchOnline(@NonNull Account account,
                                                                                         @NonNull Collection<String> searchProviderIds,
                                                                                         @NonNull String term) {
        final var searchResults = new ReactiveLiveData<Map<SearchProvider, Collection<OcsSearchResultEntry>>>();

        supplyAsync(() -> searchProviderIds, workExecutor)
                .thenApplyAsync(allowedSearchProviders -> db.getSearchProviderDao().getSearchProvider(account.getId(), allowedSearchProviders), db.getUserInteractionReadExecutor())
                .thenAcceptAsync(searchProviders -> searchProviders
                        .forEach(searchProvider -> searchOnline(account, searchProvider, term)
                                // TODO orTimeout() requires API level 31
                                .thenAcceptAsync(result -> {
                                    synchronized (searchResults) {
                                        final var fetchedResultsSoFar = Optional
                                                .ofNullable(searchResults.getValue())
                                                .orElseGet(() -> new HashMap<>(searchProviders.size()));

                                        fetchedResultsSoFar.put(searchProvider, result.entries());
                                        searchResults.postValue(fetchedResultsSoFar);
                                    }
                                }, workExecutor)
                        ), workExecutor);

        return searchResults;
    }


    private CompletableFuture<OcsSearchResult> searchOnline(@NonNull Account account,
                                                            @NonNull SearchProvider searchProvider,
                                                            @NonNull String term) {
        return requestHelper.executeOcsRequest(account, api -> api.search(null, searchProvider.getRemoteId(), term))
                .thenApplyAsync(response -> {
                    if (response.isSuccessful()) {
                        // TODO Deeper error handling
                        final var body = response.body();
                        if (body == null) {
                            throw new NullPointerException("Response body was null");
                        }

                        return body.ocs.data;
                    }

                    throw new CompletionException(new NextcloudHttpRequestFailedException(context, response.code(), Optional
                            .of(response)
                            .map(Response::errorBody)
                            .map(responseBody -> {
                                try {
                                    return new Exception(responseBody.string());
                                } catch (IOException e) {
                                    return e;
                                }
                            }).orElse(null)));
                }, workExecutor);
    }

}
