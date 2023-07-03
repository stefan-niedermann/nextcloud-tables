package it.niedermann.nextcloud.tables.remote.api;


import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.adapter.DataAdapter;
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
@SuppressWarnings("unused")
public interface TablesAPI {

    int DEFAULT_API_LIMIT = 1_000;
    int DEFAULT_API_LIMIT_TABLES = DEFAULT_API_LIMIT;
    int DEFAULT_API_LIMIT_ROWS = DEFAULT_API_LIMIT;

    String DEFAULT_TABLES_TEMPLATE = "custom";

    DateTimeFormatter FORMATTER_PROPERTIES_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    DateTimeFormatter FORMATTER_PROPERTIES_TIME = DateTimeFormatter.ISO_LOCAL_TIME;
    DateTimeFormatter FORMATTER_PROPERTIES_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(FORMATTER_PROPERTIES_DATE)
            .appendLiteral(' ')
            .append(FORMATTER_PROPERTIES_TIME)
            .toFormatter();

    DateTimeFormatter FORMATTER_DATA_DATE = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter();
    DateTimeFormatter FORMATTER_DATA_TIME = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .toFormatter();
    DateTimeFormatter FORMATTER_DATA_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(FORMATTER_PROPERTIES_DATE)
            .appendLiteral(' ')
            .append(FORMATTER_PROPERTIES_TIME)
            .toFormatter();

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
    @POST("tables")
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
    Call<Column> createColumn(@Path("tableId")
                              @Query("tableId") long tableId,
                              @Query("title") String title,
                              @Query("type") String type,
                              @Query("subtype") String subtype,
                              @Query("mandatory") boolean mandatory,
                              @Query("description") String description,
                              @Query("orderWeight") int orderWeight,
                              @Query("numberPrefix") String numberPrefix,
                              @Query("numberSuffix") String numberSuffix,
                              @Query("numberDefault") Double numberDefault,
                              @Query("numberMin") Double numberMin,
                              @Query("numberMax") Double numberMax,
                              @Query("numberDecimals") Integer numberDecimals,
                              @Query("textDefault") String textDefault,
                              @Query("textAllowedPattern") String textAllowedPattern,
                              @Query("textMaxLength") Integer textMaxLength,
                              @Query("selectionOptions") List<SelectionOption> selectionOptions,
                              @Query("selectionDefault") String selectionDefault,
                              @Query("datetimeDefault") String datetimeDefault
    );

    /**
     * @since 0.4.0
     */
    @GET("columns/{columnId}")
    Call<Column> getColumn(@Path("columnId") long columnId);

    /**
     * @since 0.4.0
     */
    @PUT("columns/{columnId}")
    Call<Column> updateColumn(@Path("columnId") long columnId,
                              @Query("title") String title,
                              @Query("mandatory") boolean mandatory,
                              @Query("description") String description,
                              @Query("orderWeight") int orderWeight,
                              @Query("numberPrefix") String numberPrefix,
                              @Query("numberSuffix") String numberSuffix,
                              @Query("numberDefault") Double numberDefault,
                              @Query("numberMin") Double numberMin,
                              @Query("numberMax") Double numberMax,
                              @Query("numberDecimals") Integer numberDecimals,
                              @Query("textDefault") String textDefault,
                              @Query("textAllowedPattern") String textAllowedPattern,
                              @Query("textMaxLength") Integer textMaxLength,
                              @Query("selectionOptions") String selectionOptions,
                              @Query("selectionDefault") String selectionDefault,
                              @Query("datetimeDefault") String datetimeDefault);

    /**
     * Due to a <a href="https://github.com/nextcloud/tables/issues/384">Bug in the Tables server app</a> the <code>mandatory</code> property will always update to <code>true</code> except the property is omitted.
     *
     * @see #updateColumn(long, String, boolean, String, int, String, String, Double, Double, Double, Integer, String, String, Integer, String, String, String)
     * @since 0.4.0
     */
    @PUT("columns/{columnId}")
    Call<Column> updateColumn(@Path("columnId") long columnId,
                              @Query("title") String title,
                              @Query("description") String description,
                              @Query("orderWeight") int orderWeight,
                              @Query("numberPrefix") String numberPrefix,
                              @Query("numberSuffix") String numberSuffix,
                              @Query("numberDefault") Double numberDefault,
                              @Query("numberMin") Double numberMin,
                              @Query("numberMax") Double numberMax,
                              @Query("numberDecimals") Integer numberDecimals,
                              @Query("textDefault") String textDefault,
                              @Query("textAllowedPattern") String textAllowedPattern,
                              @Query("textMaxLength") Integer textMaxLength,
                              @Query("selectionOptions") String selectionOptions,
                              @Query("selectionDefault") String selectionDefault,
                              @Query("datetimeDefault") String datetimeDefault);

    /**
     * @since 0.4.0
     */
    @DELETE("columns/{columnId}")
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
     * Expected format:
     * <code>
     * {
     * "1": "Foo",
     * "2": "Bar",
     * "…": "…",
     * "remoteColumnId": "value"
     * }
     * </code>
     *
     * @see DataAdapter#serialize(Data[], Type, JsonSerializationContext)
     * @since 0.4.0
     */
    @POST("tables/{tableId}/rows")
    Call<Row> createRow(@Path("tableId") long tableId,
                        @Query("data") @NonNull JsonElement data);

    /**
     * @since 0.4.0
     */
    @GET("rows/{rowId}")
    Call<Row> getRow(@Path("rowId") long rowId);

    /**
     * @since 0.4.0
     */
    @PUT("rows/{rowId}")
    Call<Row> updateRow(@Path("rowId")
                        @Query("id") long rowId,
                        @Query("data") @NonNull JsonElement data);

    /**
     * @since 0.4.0
     */
    @DELETE("rows/{rowId}")
    Call<Row> deleteRow(@Path("rowId") long rowId);
}
