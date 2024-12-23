package it.niedermann.nextcloud.tables.remote.tablesV2;


import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import android.util.Range;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.List;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateColumnResponseV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateRowResponseV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateRowV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeCollectionV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.TableV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.columns.CreateDateTimeColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.columns.CreateNumberColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.columns.CreateSelectionColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.columns.CreateTextColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.columns.CreateUserGroupColumnV2Dto;
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
public interface TablesV2API {

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

    /// The value of a `number/progress` cell is assumed to be a within this range.
    /// TODO [Create issue](https://github.com/nextcloud/deck/issues/new?template=Feature_request.md) to clarify and document this behavior.
    Range<Integer> ASSUMED_COLUMN_NUMBER_PROGRESS_DEFAULT_MAX_VALUE = new Range<>(0, 100);

    /// The assumed stars count of a `number/stars` cell.
    /// TODO [Create issue](https://github.com/nextcloud/deck/issues/new?template=Feature_request.md) to clarify and document this behavior.
    int ASSUMED_COLUMN_NUMBER_STARS_MAX_VALUE = 5;
    /* ****************************************************************************************** *

     * api_general                                                                                *
     * ****************************************************************************************** */

    /**
     * @since 0.8.0
     */
    @GET("init?format=json")
    Call<OcsResponse<List<TableV2Dto>>> init();

    /* ****************************************************************************************** *
     * api_tables                                                                                 *
     * ****************************************************************************************** */

    /**
     * @since 0.8.0
     */
    @GET("tables?format=json")
    Call<OcsResponse<List<TableV2Dto>>> getTables();

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
    Call<OcsResponse<TableV2Dto>> createTable(@Query("title") @NonNull String title,
                                              @Query("description") @NonNull String description,
                                              @Query("emoji") @NonNull String emoji,
                                              @Query("template") @NonNull String template);

    /**
     * @since 0.8.0
     */
    @GET("tables/{tableId}?format=json")
    Call<OcsResponse<TableV2Dto>> getTable(@Path("tableId") long tableId);

    /**
     * @since 0.8.0
     */
    @PUT("tables/{tableId}?format=json")
    Call<OcsResponse<TableV2Dto>> updateTable(@Path("tableId") long tableId,
                                              @Query("title") @NonNull String title,
                                              @Query("description") @NonNull String description,
                                              @Query("emoji") @NonNull String emoji);

    /**
     * @since 0.8.0
     */
    @DELETE("tables/{tableId}?format=json")
    Call<OcsResponse<TableV2Dto>> deleteTable(@Path("tableId") long tableId);

    /* ****************************************************************************************** *
     * api_columns                                                                                *
     * ****************************************************************************************** */

    /**
     * @since 0.8.0
     */
    @GET("columns/{nodeType}/{nodeId}?format=json")
    Call<OcsResponse<List<ColumnV2Dto>>> getColumns(@Path("nodeType") @NonNull ENodeTypeV2Dto nodeType,
                                                    @Path("nodeId") long nodeId);

    /**
     * @since 0.8.0
     */
    @GET("columns/{columnRemoteId}?format=json")
    Call<OcsResponse<ColumnV2Dto>> getColumn(@Path("columnRemoteId") long columnId);

    /**
     * @since 0.8.0
     */
    @POST("columns/number?format=json")
    Call<OcsResponse<CreateColumnResponseV2Dto>> createNumberColumn(@Body @NonNull CreateNumberColumnV2Dto column);

    /**
     * @since 0.8.0
     */
    @POST("columns/text?format=json")
    Call<OcsResponse<CreateColumnResponseV2Dto>> createTextColumn(@Body @NonNull CreateTextColumnV2Dto column);

    /**
     * @since 0.8.0
     */
    @POST("columns/selection?format=json")
    Call<OcsResponse<CreateColumnResponseV2Dto>> createSelectionColumn(@Body @NonNull CreateSelectionColumnV2Dto column);

    /**
     * @since 0.8.0
     */
    @POST("columns/datetime?format=json")
    Call<OcsResponse<CreateColumnResponseV2Dto>> createDateTimeColumn(@Body @NonNull CreateDateTimeColumnV2Dto column);

    /**
     * @since 0.8.0
     */
    @POST("columns/usergroup?format=json")
    Call<OcsResponse<CreateColumnResponseV2Dto>> createUserGroupColumn(@Body @NonNull CreateUserGroupColumnV2Dto column);

    /* ****************************************************************************************** *
     * api_favorite                                                                               *
     * ****************************************************************************************** */

    @POST("favorites/{nodeType}/{nodeId}")
    Call<OcsResponse<?>> setFavorite(@Path("nodeType") @NonNull ENodeTypeV2Dto nodeType,
                                     @Path("nodeId") long nodeId);

    @DELETE("favorites/{nodeType}/{nodeId}")
    Call<OcsResponse<?>> unsetFavorite(@Path("nodeType") @NonNull ENodeTypeV2Dto nodeType,
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
    Call<OcsResponse<CreateRowResponseV2Dto>> createRow(
            @Path("nodeCollection") @NonNull ENodeCollectionV2Dto nodeCollection,
            @Path("nodeId") long tableId,
            @Body @NonNull CreateRowV2Dto data);

}
