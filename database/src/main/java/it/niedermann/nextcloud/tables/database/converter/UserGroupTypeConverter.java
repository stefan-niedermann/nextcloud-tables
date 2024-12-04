package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import it.niedermann.nextcloud.tables.database.model.EUserGroupType;

public class UserGroupTypeConverter {

    @TypeConverter
    public static EUserGroupType userGroupTypeFromInteger(@Nullable Integer type) {
        if (type == null) {
            return EUserGroupType.UNKNOWN;
        }

        return EUserGroupType.findByRemoteId(type);
    }

    @TypeConverter
    public static Integer userGroupTypeToInteger(@Nullable EUserGroupType type) {
        if (type == null || EUserGroupType.UNKNOWN.equals(type)) {
            return null;
        }

        return type.getRemoteId();
    }
}
