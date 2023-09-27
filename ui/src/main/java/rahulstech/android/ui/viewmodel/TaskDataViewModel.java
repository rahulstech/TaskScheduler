package rahulstech.android.ui.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.dao.TaskDataDao;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.util.concurrent.AsyncTasksManager;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

public class TaskDataViewModel extends AndroidViewModel {

    private final TaskDataDao mDao;

    private final AsyncTasksManager mTaskManager = new AsyncTasksManager();

    public TaskDataViewModel(@NonNull Application application) {
        super(application);

        SchedulerDB db = SchedulerDB.getInstance(application);
        mDao = db.getTaskDataDao();
    }

    @NonNull
    public AsyncTasksManager getAsyncTaskManaget() {
        return mTaskManager;
    }


    @SuppressLint("StaticFieldLeak")
    public ListenableAsyncTask<Void,Void, List<TaskData>> addTaskData(@NonNull List<TaskData> data) {
        return new ListenableAsyncTask<Void, Void, List<TaskData>>(getAsyncTaskManaget()) {

            @Override
            protected List<TaskData> onExecuteInBackground(Void... voids) throws Exception {
                long[] ids = mDao.insertTaskData(data);
                if (null != ids) {
                    for (int i = 0; i < ids.length; i++) {
                        data.get(i).setId(ids[i]);
                    }
                    return data;
                }
                throw new RuntimeException("unable to insert tasks="+data);
            }
        };
    }

    @SuppressLint("StaticFieldLeak")
    public ListenableAsyncTask<Void,Void, List<TaskData>> setTaskData(@NonNull List<TaskData> data) {
        return new ListenableAsyncTask<Void, Void, List<TaskData>>(getAsyncTaskManaget()) {
            @Override
            protected List<TaskData> onExecuteInBackground(Void... voids) throws Exception {
                int changes = mDao.updateTaskData(data);
                if (changes == data.size()) {
                    return data;
                }
                throw new RuntimeException("unable to update tasks="+data);
            }
        };
    }

    @SuppressLint("StaticFieldLeak")
    public ListenableAsyncTask<Void,Void, List<TaskData>> removeTaskData(@NonNull List<TaskData> data) {
        return new ListenableAsyncTask<Void, Void, List<TaskData>>(getAsyncTaskManaget()) {
            @Override
            protected List<TaskData> onExecuteInBackground(Void... voids) throws Exception {
                int changes = mDao.deleteTaskData(data);
                if (changes == data.size()) {
                    return data;
                }
                throw new RuntimeException("unable to delete tasks="+data);
            }
        };
    }
}
