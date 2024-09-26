package it.niedermann.nextcloud.tables.remote.api;


import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.nextcloud.android.sso.model.ocs.OcsResponse;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.model.ENodeType;
import it.niedermann.nextcloud.tables.remote.model.columns.DateTimeColumn;
import it.niedermann.nextcloud.tables.remote.model.columns.NumberColumn;
import it.niedermann.nextcloud.tables.remote.model.columns.SelectionColumn;
import it.niedermann.nextcloud.tables.remote.model.columns.TextColumn;
import it.niedermann.nextcloud.tables.remote.model.columns.UserGroupColumn;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @link <a href="https://petstore.swagger.io/?url=https://raw.githubusercontent.com/nextcloud/tables/refs/heads/main/openapi.json">Tables REST API</a>
 */
@SuppressWarnings("unused")
public interface TablesAPI {

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

    /* ****************************************************************************************** *
     * api_general                                                                                *
     * ****************************************************************************************** */

    /**
     * @since 0.8.0
     */
    @GET("init?format=json")
    Call<OcsResponse<List<Table>>> init();

    /* ****************************************************************************************** *
     * api_tables                                                                                 *
     * ****************************************************************************************** */

    /**
     * @since 0.8.0
     */
    @GET("tables?format=json")
    Call<OcsResponse<List<Table>>> getTables();

//    See https://github.com/nextcloud/tables/issues/1180#issuecomment-2376202656
//
//    /**
//     * @since 0.8.0
//     */
//    @GET("tables?format=json")
//    Call<OcsResponse<List<Table>>> getTables(@Query("limit") int limit,
//                                             @Query("offset") int offset);

    /**
     * @since 0.8.0
     */
    @POST("tables?format=json")
    Call<OcsResponse<Table>> createTable(@Query("title") @NonNull String title,
                                         @Query("description") @NonNull String description,
                                         @Query("emoji") @NonNull String emoji,
                                         @Query("template") @NonNull String template);

    /**
     * @since 0.8.0
     */
    @GET("tables/{tableId}?format=json")
    Call<OcsResponse<Table>> getTable(@Path("tableId") long tableId);

    /**
     * @since 0.8.0
     */
    @PUT("tables/{tableId}?format=json")
    Call<OcsResponse<Table>> updateTable(@Path("tableId") long tableId,
                                         @Query("title") @NonNull String title,
                                         @Query("description") @NonNull String description,
                                         @Query("emoji") @NonNull String emoji);

    /**
     * @since 0.8.0
     */
    @DELETE("tables/{tableId}?format=json")
    Call<OcsResponse<Table>> deleteTable(@Path("tableId") long tableId);

    /* ****************************************************************************************** *
     * api_columns                                                                                *
     * ****************************************************************************************** */

    /**
     * @since 0.8.0
     */
    @GET("/columns/{nodeType}/{nodeId}?format=json")
    Call<OcsResponse<List<Column>>> getColumns(@Path("nodeType") @NonNull ENodeType nodeType,
                                               @Path("nodeId") long nodeId);

    /**
     * @since 0.8.0
     */
    @GET("columns/{columnId}?format=json")
    Call<OcsResponse<Column>> getColumn(@Path("columnId") long columnId);

    /**
     * @since 0.8.0
     */
    @POST("columns/number?format=json")
    Call<OcsResponse<Column>> createNumberColumn(@Body @NonNull NumberColumn column);

    /**
     * @since 0.8.0
     */
    @POST("columns/text?format=json")
    Call<OcsResponse<Column>> createTextColumn(@Body @NonNull TextColumn column);

    /**
     * @since 0.8.0
     */
    @POST("columns/selection?format=json")
    Call<OcsResponse<Column>> createSelectionColumn(@Body @NonNull SelectionColumn column);

    /**
     * @since 0.8.0
     */
    @POST("columns/datetime?format=json")
    Call<OcsResponse<Column>> createDateTimeColumn(@Body @NonNull DateTimeColumn column);

    /**
     * @since 0.8.0
     */
    @POST("columns/usergroup?format=json")
    Call<OcsResponse<Column>> createUserGroupColumn(@Body @NonNull UserGroupColumn column);

    /* ****************************************************************************************** *
     * api_favorite                                                                               *
     * ****************************************************************************************** */

    @POST("favorites/{nodeType}/{nodeId}")
    Call<OcsResponse<?>> setFavorite(@Path("nodeType") @NonNull ENodeType nodeType,
                                     @Path("nodeId") long nodeId);

    @DELETE("favorites/{nodeType}/{nodeId}")
    Call<OcsResponse<?>> unsetFavorite(@Path("nodeType") @NonNull ENodeType nodeType,
                                       @Path("nodeId") long nodeId);

    /* ****************************************************************************************** *
     * context                                                                                    *
     * ****************************************************************************************** */

    /* ****************************************************************************************** *
     * rowocs                                                                                     *
     * ****************************************************************************************** */

    /**
     * @since 0.8.0
     */
    @POST("{nodeCollection}/{nodeId}/rows?format=json")
    Call<OcsResponse<Row>> createRow(
            @Path("nodeCollection") @NonNull ENodeType nodeType,
            @Path("nodeId") long tableId,
            @Query("data") @NonNull JsonElement data);

}
