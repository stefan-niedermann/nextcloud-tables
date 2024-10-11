package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import it.niedermann.nextcloud.tables.database.model.EDataType;

public class EDataTypeConverter {

    @TypeConverter
    public static EDataType databaseTypeFromString(@Nullable Integer databaseType) {
        if (databaseType == null) {
            return EDataType.UNKNOWN;
        }

        try {
            return EDataType.findById(databaseType);
        } catch (NumberFormatException e) {
            return EDataType.UNKNOWN;
        }
    }

    @TypeConverter
    public static Integer databaseTypeToString(@Nullable EDataType databaseType) {
        if (databaseType == null || EDataType.UNKNOWN.equals(databaseType)) {
            return null;
        }

        return databaseType.getId();
    }

}
