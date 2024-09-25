package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import it.niedermann.nextcloud.tables.database.model.NextcloudVersion;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Version;

public class VersionConverter {

    @TypeConverter
    public static Version versionFromString(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return Version.parse(value);
    }

    @TypeConverter
    public static String versionToString(@Nullable Version value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @TypeConverter
    public static NextcloudVersion nextcloudVersionFromString(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return NextcloudVersion.parse(value);
    }

    @TypeConverter
    public static String nextcloudVersionToString(@Nullable NextcloudVersion value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @TypeConverter
    public static TablesVersion tablesVersionFromString(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return TablesVersion.parse(value);
    }

    @TypeConverter
    public static String tablesVersionToString(@Nullable TablesVersion value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
