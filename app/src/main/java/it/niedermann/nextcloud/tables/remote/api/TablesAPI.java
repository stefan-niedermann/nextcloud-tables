package it.niedermann.nextcloud.tables.remote.api;


import androidx.annotation.NonNull;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @link <a href="https://petstore.swagger.io/?url=https://raw.githubusercontent.com/nextcloud/tables/master/APIv1.yaml#/">Tables REST API</a>
 */
public interface TablesAPI {

    int API_LIMIT_DEFAULT = 1_000;
    int API_LIMIT_TABLES = API_LIMIT_DEFAULT;
    int API_LIMIT_ROWS = API_LIMIT_DEFAULT;

    /**
     * @since 0.3.0
     */
    @GET("tables")
    Call<List<Table>> getTables();

    /**
     * @since 0.3.0
     */
    @GET("tables")
    Call<List<Table>> getTables(@Query("limit") int limit,
                                @Query("offset") int offset);

    /**
     * @since 0.3.0
     */
    @GET("tables")
    Call<List<Table>> getTables(@Query("keyword") @NonNull String keyword,
                                @Query("limit") int limit,
                                @Query("offset") int offset);

    /**
     * @since 0.4.0
     */
    @GET("tables/{tableId}")
    Call<Table> getTable(@Path("tableId") long tableId);

    /**
     * @since 0.4.0
     */
    @POST("table")
    Call<Table> createTable(@Query("title") @NonNull String title,
                            @Query("emoji") @NonNull String emoji,
                            @Query("template") @NonNull String template);

    /**
     * @since 0.4.0
     */
    @DELETE("tables/{tableId}")
    Call<Table> deleteTable(@Path("tableId") long tableId);

    /**
     * @since 0.4.0
     */
    @PUT("tables/{tableId}")
    Call<Table> updateTable(@Path("tableId") long tableId,
                            @Query("title") @NonNull String title,
                            @Query("emoji") @NonNull String emoji,
                            @Query("template") @NonNull String template);

    /**
     * @since 0.4.0
     */
    @PUT("tables/{tableId}")
    Call<Table> updateTable(@Path("tableId") long tableId,
                            @Query("title") @NonNull String title,
                            @Query("emoji") @NonNull String emoji);

    /**
     * @since 0.4.0
     */
    @GET("tables/{tableId}/columns")
    Call<List<Column>> getColumns(@Path("tableId") long tableId);

    /**
     * @since 0.4.0
     */
    @POST("tables/{tableId}/columns")
    Call<Column> createColumn(@Path("tableId") long tableId,
                              @Query("values") @NonNull Column column);

    /**
     * @since 0.4.0
     */
    @GET("column/{columnId}")
    Call<Column> getColumn(@Path("columnId") long columnId);

    /**
     * @since 0.4.0
     */
    @PUT("column/{columnId}")
    Call<Column> updateColumn(@Path("columnId") long columnId,
                              @Query("values") @NonNull Column column);

    /**
     * @since 0.4.0
     */
    @DELETE("column/{columnId}")
    Call<Column> deleteColumn(@Path("columnId") long columnId);

    /**
     * @since 0.4.0
     */
    @GET("tables/{tableId}/rows")
    Call<List<Row>> getRows(@Path("tableId") long tableId);

    /**
     * @since 0.4.0
     */
    @GET("tables/{tableId}/rows")
    Call<List<Row>> getRows(@Path("tableId") long tableId,
                            @Query("limit") int limit,
                            @Query("offset") int offset);

    /**
     * @since 0.4.0
     */
    @POST("tables/{tableId}/rows")
    Call<Row> createRow(@Path("tableId") long tableId,
                        @Query("data") @NonNull Row data);

    /**
     * @since 0.4.0
     */
    @GET("rows/{rowId}")
    Call<Row> getRow(@Path("rowId") long rowId);

    /**
     * @since 0.4.0
     */
    @PUT("rows/{rowId}")
    Call<Row> updateRow(@Path("rowId") long rowId,
                        @Query("data") @NonNull Row data);

    /**
     * @since 0.4.0
     */
    @DELETE("rows/{rowId}")
    Call<Row> deleteRow(@Path("rowId") long rowId);
}
