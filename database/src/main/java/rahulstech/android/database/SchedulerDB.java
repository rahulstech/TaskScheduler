package rahulstech.android.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.BuiltInTypeConverters;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import rahulstech.android.database.dao.TaskDao;
import rahulstech.android.database.dao.TaskDataDao;
import rahulstech.android.database.datatype.DatatypeConverters;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.migration.MIGRATION_1_2;
import rahulstech.android.database.view.TaskBriefView;

@Database(entities = {Task.class, TaskData.class},
        views = {TaskBriefView.class},
        version = SchedulerDB.DB_VERSION)
@TypeConverters(value = {DatatypeConverters.class},
        builtInTypeConverters = @BuiltInTypeConverters())
public abstract class SchedulerDB extends RoomDatabase {

    private static final String TAG = "ScheduleDB";

    static final int DB_VERSION = 2;

    private static SchedulerDB mInstance = null;

    @NonNull
    public static SchedulerDB getInstance(@NonNull Context ctx) {
        if (null == mInstance) {
            try {
                mInstance = Room.databaseBuilder(ctx.getApplicationContext(), SchedulerDB.class,
                                "scheduler_db.sqlite3")
                        .addMigrations(new MIGRATION_1_2())
                        .build();
            }
            catch (Throwable th) {
                throw new RuntimeException(th);
            }
        }
        return mInstance;
    }

    public abstract TaskDao getTaskDao();

    public abstract TaskDataDao getTaskDataDao();
}
