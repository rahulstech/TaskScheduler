package rahulstech.android.taskschedulder.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.dao.TaskDataDao;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

@SuppressLint({"StaticFieldLeak","unused"})
public class TaskDataViewModel extends BaseViewModel {

    public static final String TAG_REMOVE_TASK_DATA = "removeTaskData";

    public static final String TAG_SET_TASK_DATA = "setTaskData";

    public static final String TAG_ADD_TASK_DATA = "addTaskData";

    private final TaskDataDao mDao;

    public TaskDataViewModel(@NonNull Application application) {
        super(application);

        SchedulerDB db = SchedulerDB.getInstance(application);
        mDao = db.getTaskDataDao();
    }

    @NonNull
    public TaskDataDao getTaskDataDao() {
        return mDao;
    }

    public void addTaskData(@NonNull List<TaskData> data) {
        new ListenableAsyncTask<Void, Void, List<TaskData>>(getAsyncTaskManager(),TAG_ADD_TASK_DATA) {
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
        }.start();
    }

    public void setTaskData(@NonNull List<TaskData> data) {
        new ListenableAsyncTask<Void, Void, List<TaskData>>(getAsyncTaskManager(),TAG_SET_TASK_DATA) {
            @Override
            protected List<TaskData> onExecuteInBackground(Void... voids) throws Exception {
                int changes = mDao.updateTaskData(data);
                if (changes == data.size()) {
                    return data;
                }
                throw new RuntimeException("unable to update task-data="+data);
            }
        }.start();
    }

    public void removeTaskData(@NonNull List<TaskData> data) {
        new ListenableAsyncTask<Void, Void, List<TaskData>>(getAsyncTaskManager(),TAG_REMOVE_TASK_DATA) {
            @Override
            protected List<TaskData> onExecuteInBackground(Void... voids) throws Exception {
                int changes = mDao.deleteTaskData(data);
                if (changes == data.size()) {
                    return data;
                }
                throw new RuntimeException("unable to delete task-data="+data);
            }
        }.start();
    }
}
