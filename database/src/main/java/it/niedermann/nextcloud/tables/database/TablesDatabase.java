package it.niedermann.nextcloud.tables.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
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
import it.niedermann.nextcloud.tables.database.dao.DefaultValueSelectionOptionCrossRefDao;
import it.niedermann.nextcloud.tables.database.dao.LinkValueDao;
import it.niedermann.nextcloud.tables.database.dao.RowDao;
import it.niedermann.nextcloud.tables.database.dao.SearchProviderDao;
import it.niedermann.nextcloud.tables.database.dao.SelectionOptionDao;
import it.niedermann.nextcloud.tables.database.dao.TableDao;
import it.niedermann.nextcloud.tables.database.dao.UserGroupDao;
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
import it.niedermann.nextcloud.tables.database.migration.Migration_3_4;
import it.niedermann.nextcloud.tables.database.migration.Migration_4_5;
import it.niedermann.nextcloud.tables.database.migration.Migration_5_6;
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
        autoMigrations = {
                @AutoMigration(from = 3, to = 4, spec = Migration_3_4.class),
                @AutoMigration(from = 4, to = 5, spec = Migration_4_5.class),
        },
        version = 6
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
                        new Migration_2_3(),
                        new Migration_5_6()
                )
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .fallbackToDestructiveMigration(true)
                .build();
    }

    /// Use for any non changing database requests as select statements where the order of statements does not matter
    public ExecutorService getParallelExecutor() {
        return parallelExecutor;
    }

    /// Use for writing sequentially to the database while synchronization to not block working with the app
    public ExecutorService getSequentialWriteExecutorForSync() {
        return sequentialExecutor;
    }

    /// Use for writing sequentially to the database on user interaction
    public ExecutorService getSequentialWriteExecutor() {
        return sequentialExecutor;
    }

    public abstract AccountDao getAccountDao();

    public abstract TableDao getTableDao();

    public abstract ColumnDao getColumnDao();

    public abstract SelectionOptionDao getSelectionOptionDao();

    public abstract RowDao getRowDao();

    public abstract DataDao getDataDao();

    public abstract DefaultValueSelectionOptionCrossRefDao getDefaultValueSelectionOptionCrossRefDao();

    public abstract DataSelectionOptionCrossRefDao getDataSelectionOptionCrossRefDao();

    public abstract DataUserGroupCrossRefDao getDataUserGroupCrossRefDao();

    public abstract SearchProviderDao getSearchProviderDao();

    public abstract UserGroupDao getUserGroupDao();

    public abstract LinkValueDao getLinkValueDao();
}
