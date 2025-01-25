package it.niedermann.nextcloud.tables.repository;

import static java.util.Collections.emptyMap;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.util.Patterns;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.remote.RequestHelper;
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

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteAccount(@NonNull Account account) {
        return runAsync(() -> db.getAccountDao().delete(account), db.getSequentialExecutor());
    }

    @NonNull
    public LiveData<OcsSearchResultEntry> searchUrl(@NonNull Account account,
                                                    @NonNull String term) {
        final var searchResult = new ReactiveLiveData<OcsSearchResultEntry>();
        final var isValidUri = Patterns.WEB_URL.matcher(term).matches();

        if (isValidUri) {
            searchResult.postValue(new OcsSearchResultEntry(null, term, "Url", term, null, false, emptyMap()));
        }

//        requestHelper.executeNetworkRequest(account, apiTuple -> apiTuple.ocs().resolve(null, Uri.parse(term)))
//                .whenCompleteAsync((result, exception) -> {
//                    if (result != null && exception == null) {
//                        // TODO post result as new searchResult value
//                    }
//                });

        return searchResult;
    }

    @NonNull
    public LiveData<Map<SearchProvider, Collection<OcsSearchResultEntry>>> search(@NonNull Account account,
                                                                                  @NonNull Column column,
                                                                                  @NonNull String term) {
        final var searchResults = new ReactiveLiveData<Map<SearchProvider, Collection<OcsSearchResultEntry>>>(Collections.emptyMap());

        supplyAsync(() -> Optional.of(column)
                .map(Column::getTextAttributes)
                .map(TextAttributes::textAllowedPattern)
                .map(pattern -> pattern.split(","))
                .map(Set::of)
                .orElseGet(Collections::emptySet), workExecutor)
                .thenApplyAsync(allowedSearchProviders -> db.getSearchProviderDao().getSearchProvider(column.getAccountId(), allowedSearchProviders), db.getParallelExecutor())
                .thenAcceptAsync(searchProviders -> searchProviders
                        .forEach(searchProvider -> search(account, searchProvider, term)
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


    public CompletableFuture<OcsSearchResult> search(@NonNull Account account,
                                                     @NonNull SearchProvider searchProvider,
                                                     @NonNull String term) {
        return requestHelper.executeNetworkRequest(account, apiTuple -> apiTuple.ocs().search(null, searchProvider.getRemoteId(), term))
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
