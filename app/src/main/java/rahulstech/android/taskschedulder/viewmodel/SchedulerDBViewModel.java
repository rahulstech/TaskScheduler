package rahulstech.android.taskschedulder.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.dao.TaskDao;
import rahulstech.android.database.dao.TaskDataDao;
import rahulstech.android.database.entity.TaskData;

public class SchedulerDBViewModel extends BaseViewModel {

    private SchedulerDB mDB;

    public SchedulerDBViewModel(@NonNull Application application) {
        super(application);
        mDB = SchedulerDB.getInstance(application);
    }

    @NonNull
    public SchedulerDB getSchedulerDB() {
        return mDB;
    }

    @NonNull
    public TaskDao getTaskDao() {
        return mDB.getTaskDao();
    }

    @NonNull
    public TaskDataDao getTaskDataDao() {
        return mDB.getTaskDataDao();
    }
}
