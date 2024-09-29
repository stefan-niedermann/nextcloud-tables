package it.niedermann.nextcloud.tables.remote.tablesV1;


import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.RowV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateColumnV1Dto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @link <a href="https://petstore.swagger.io/?url=https://github.com/nextcloud/tables/blob/main/openapi.json">Tables REST API</a>
 */
@SuppressWarnings("unused")
public interface TablesV1API {

    DateTimeFormatter FORMATTER_PROPERTIES_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    DateTimeFormatter FORMATTER_PROPERTIES_TIME = DateTimeFormatter.ISO_LOCAL_TIME;
    DateTimeFormatter FORMATTER_PROPERTIES_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(FORMATTER_PROPERTIES_DATE)
            .appendLiteral(' ')
            .append(FORMATTER_PROPERTIES_TIME)
            .toFormatter();
    int DEFAULT_API_LIMIT_ROWS = 1_000;

    /**
     * @since 0.4.0
     */
    @PUT("columns/{columnId}")
    Call<ColumnV1Dto> updateColumn(@Path("columnId") long columnId,
                                   @Body() UpdateColumnV1Dto column);

    /**
     * @since 0.4.0
     */
    @DELETE("columns/{columnId}")
    Call<ColumnV1Dto> deleteColumn(@Path("columnId") long columnId);


    /**
     * @since 0.4.0
     */
    @GET("tables/{tableId}/rows")
    Call<List<RowV1Dto>> getRows(@Path("tableId") long tableId,
                                 @Query("limit") int limit,
                                 @Query("offset") int offset);

    /**
     * @since 0.4.0
     */
    @PUT("rows/{rowId}")
    Call<RowV1Dto> updateRow(@Path("rowId")
                             @Query("id") long rowId,
                             @Query("data") @NonNull JsonElement data);

    /**
     * @since 0.4.0
     */
    @DELETE("rows/{rowId}")
    Call<RowV1Dto> deleteRow(@Path("rowId") long rowId);
}
