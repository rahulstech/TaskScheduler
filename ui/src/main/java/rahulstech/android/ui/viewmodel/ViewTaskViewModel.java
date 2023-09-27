package rahulstech.android.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.dao.TaskDao;
import rahulstech.android.database.model.TaskWithTaskDataModel;
import rahulstech.android.util.concurrent.AsyncTasksManager;

public class ViewTaskViewModel extends TaskViewModel {

    TaskDao mTaskDao;

    LiveData<TaskWithTaskDataModel> mTaskWithData;

    AsyncTasksManager mTaskManager = new AsyncTasksManager();

    public ViewTaskViewModel(@NonNull Application application) {
        super(application);
        SchedulerDB db = SchedulerDB.getInstance(application);
        mTaskDao = db.getTaskDao();
    }

    @NonNull
    public AsyncTasksManager getAsyncTaskManager() {
        return mTaskManager;
    }

    @NonNull
    public LiveData<TaskWithTaskDataModel> getTaskWithDataByTaskId(long id) {
        if (null == mTaskWithData) {
            mTaskWithData = mTaskDao.findLiveTaskWithTaskDataModelByTaskId(id);
        }
        return mTaskWithData;
    }
}
