package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public class EDataTypeConverter {

    @TypeConverter
    public static EDataType databaseTypeFromString(@Nullable Integer dataType) {
        if (dataType == null) {
            return EDataType.UNKNOWN;
        }

        try {
            return EDataType.findById(dataType);

        } catch (NumberFormatException e) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }

            return EDataType.UNKNOWN;
        }
    }

    @TypeConverter
    public static Integer databaseTypeToString(@Nullable EDataType dataType) {
        if (dataType == null || EDataType.UNKNOWN.equals(dataType)) {
            return null;
        }

        return dataType.getId();
    }

}
