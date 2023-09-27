package rahulstech.android.ui.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.dao.TaskDao;
import rahulstech.android.database.entity.Task;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

@SuppressWarnings("unused")
public class TaskViewModel extends BaseViewModel {

    private final TaskDao mTaskDao;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        SchedulerDB db = SchedulerDB.getInstance(application);
        mTaskDao = db.getTaskDao();
    }

    @NonNull
    public TaskDao getTaskDao() {
        return mTaskDao;
    }

    @SuppressLint("StaticFieldLeak")
    public ListenableAsyncTask<Void, Void, Task> editTask(@NonNull final Task task) {
        return new ListenableAsyncTask<Void, Void, Task>(getAsyncTaskManager()) {
            @Override
            protected Task onExecuteInBackground(Void... voids) throws Exception {
                int changes = getTaskDao().setTask(task);
                if (1 == changes) {
                    return task;
                }
                throw new RuntimeException("unable to edit task");
            }
        };
    }

    @SuppressWarnings("StaticFieldLeak")
    @NonNull
    public ListenableAsyncTask<Void,Void,Boolean> removeTask(long taskId) {
        return new ListenableAsyncTask<Void, Void, Boolean>(getAsyncTaskManager()) {
            @Override
            protected Boolean onExecuteInBackground(Void... voids) throws Exception {
                Task task = new Task();
                task.setId(taskId);
                return 1 == getTaskDao().removeTask(task);
            }
        };
    }
}
