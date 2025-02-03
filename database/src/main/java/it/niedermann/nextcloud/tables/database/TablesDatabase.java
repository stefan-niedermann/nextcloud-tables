package it.niedermann.nextcloud.tables.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.tables.database.converter.DBStatusConverter;
import it.niedermann.nextcloud.tables.database.converter.EDataTypeConverter;
import it.niedermann.nextcloud.tables.database.converter.InstantConverter;
import it.niedermann.nextcloud.tables.database.converter.JsonElementConverter;
import it.niedermann.nextcloud.tables.database.converter.LocalDateConverter;
import it.niedermann.nextcloud.tables.database.converter.LocalTimeConverter;
import it.niedermann.nextcloud.tables.database.converter.UriConverter;
import it.niedermann.nextcloud.tables.database.converter.UserGroupTypeConverter;
import it.niedermann.nextcloud.tables.database.converter.VersionConverter;
import it.niedermann.nextcloud.tables.database.dao.AccountDao;
import it.niedermann.nextcloud.tables.database.dao.ColumnDao;
import it.niedermann.nextcloud.tables.database.dao.DataDao;
import it.niedermann.nextcloud.tables.database.dao.DataSelectionOptionCrossRefDao;
import it.niedermann.nextcloud.tables.database.dao.DataUserGroupCrossRefDao;
import it.niedermann.nextcloud.tables.database.dao.LinkValueDao;
import it.niedermann.nextcloud.tables.database.dao.RowDao;
import it.niedermann.nextcloud.tables.database.dao.SearchProviderDao;
import it.niedermann.nextcloud.tables.database.dao.SelectionOptionDao;
import it.niedermann.nextcloud.tables.database.dao.TableDao;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.DataSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.DataUserGroupCrossRef;
import it.niedermann.nextcloud.tables.database.entity.DefaultValueSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.DefaultValueUserGroupCrossRef;
import it.niedermann.nextcloud.tables.database.entity.LinkValue;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.entity.TextAllowedPattern;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.migration.Migration_1_2;
import it.niedermann.nextcloud.tables.database.migration.Migration_2_3;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;

@Database(
        entities = {
                Account.class,
                Table.class,
                Column.class,
                Row.class,
                Data.class,
                SelectionOption.class,
                UserGroup.class,
                DataSelectionOptionCrossRef.class,
                DataUserGroupCrossRef.class,
                DefaultValueSelectionOptionCrossRef.class,
                DefaultValueUserGroupCrossRef.class,
                SearchProvider.class,
                TextAllowedPattern.class,
                LinkValue.class,
        },
     // autoMigrations = {
     //     @AutoMigration(from = 3, to = 4, spec = Migration_3_4.class)
     // },
        version = 3
)
@TypeConverters({
        DBStatusConverter.class,
        InstantConverter.class,
        LocalDateConverter.class,
        LocalTimeConverter.class,
        JsonElementConverter.class,
        VersionConverter.class,
        UserGroupTypeConverter.class,
        EDataTypeConverter.class,
        UriConverter.class,
})
public abstract class TablesDatabase extends RoomDatabase {

    private static final String TAG = TablesDatabase.class.getSimpleName();
    private static final String DB_NAME = "nextcloud-tables.sqlite";

    private static final ExecutorService DB_PARALLEL = SharedExecutors.getIODbOutputExecutor();
    private static final ExecutorService DB_SEQUENTIAL = SharedExecutors.getIODbInputExecutor();

    private final ExecutorService parallelExecutor;
    private final ExecutorService sequentialExecutor;

    private static volatile TablesDatabase instance;

    public TablesDatabase() {
        this.parallelExecutor = DB_PARALLEL;
        this.sequentialExecutor = DB_SEQUENTIAL;
    }

    public static synchronized TablesDatabase getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = create(context.getApplicationContext());
        }
        return instance;
    }

    private static TablesDatabase create(@NonNull final Context context) {
        return Room.databaseBuilder(context, TablesDatabase.class, DB_NAME)
                .addMigrations(
                        new Migration_1_2(),
                        new Migration_2_3()
                )
                .fallbackToDestructiveMigrationOnDowngrade()
                // .fallbackToDestructiveMigration()
                .build();
    }

    public ExecutorService getParallelExecutor() {
        return parallelExecutor;
    }

    public ExecutorService getSequentialExecutor() {
        return sequentialExecutor;
    }

    public abstract AccountDao getAccountDao();

    public abstract TableDao getTableDao();

    public abstract ColumnDao getColumnDao();

    public abstract SelectionOptionDao getSelectionOptionDao();

    public abstract RowDao getRowDao();

    public abstract DataDao getDataDao();

    public abstract DataSelectionOptionCrossRefDao getDataSelectionOptionCrossRefDao();

    public abstract DataUserGroupCrossRefDao getDataUserGroupCrossRefDao();

    public abstract SearchProviderDao getSearchProviderDao();

    public abstract LinkValueDao getLinkValueDao();
}
