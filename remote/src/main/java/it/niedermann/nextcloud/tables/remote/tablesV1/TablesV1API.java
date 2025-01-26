package it.niedermann.nextcloud.tables.remote.tablesV1;


import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.List;

import it.niedermann.nextcloud.tables.remote.shared.model.RemoteDto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnRequestV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.FetchRowResponseV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateColumnResponseV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateRowRequestV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateRowResponseV1Dto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @link <a href="https://petstore.swagger.io/?url=https://raw.githubusercontent.com/nextcloud/tables/refs/heads/main/openapi.json">Tables REST API</a>
 */
@SuppressWarnings("unused")
public interface TablesV1API {

    int DEFAULT_API_LIMIT_ROWS = 1_000;

    /// Though not available as a SearchProvider, this is a valid `providerId` that can be used in `text/link` columns
    String TEXT_LINK_PROVIDER_ID_URL = "url";

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
     * @since 0.4.0
     */
    @PUT("columns/{columnRemoteId}")
    Call<UpdateColumnResponseV1Dto> updateColumn(@Path("columnRemoteId") long columnId,
                                                 @Body() ColumnRequestV1Dto column);

    /**
     * @since 0.4.0
     */
    @DELETE("columns/{columnRemoteId}")
    Call<RemoteDto> deleteColumn(@Path("columnRemoteId") long columnId);


    /**
     * @since 0.4.0
     */
    @GET("tables/{tableId}/rows")
    Call<List<FetchRowResponseV1Dto>> getRows(@Path("tableId") long tableId,
                                              @Query("limit") int limit,
                                              @Query("offset") int offset);

    /**
     * @since 0.4.0
     */
    @PUT("rows/{rowId}")
    Call<UpdateRowResponseV1Dto> updateRow(@Path("rowId")
                                           @Query("id") long rowId,
                                           @Body @NonNull UpdateRowRequestV1Dto payload);

    /**
     * @since 0.4.0
     */
    @DELETE("rows/{rowId}")
    Call<?> deleteRow(@Path("rowId") long rowId);
}
