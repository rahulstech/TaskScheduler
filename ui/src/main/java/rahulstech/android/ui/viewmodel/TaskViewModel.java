package rahulstech.android.ui.viewmodel;

import android.app.Application;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.dao.TaskDao;
import rahulstech.android.database.datatype.TaskState;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

@SuppressWarnings({"unused","StaticFieldLeak"})
public class TaskViewModel extends BaseViewModel {

    public static final String TAG_REMOVE_TASKS = "removeTasks";

    public static String TAG_EDIT_TASK = "editTask";

    public static String TAG_CHANGE_TASK_STATE = "changeTaskState";

    private final TaskDao mTaskDao;

    private LiveData<Task> mTask = null;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        SchedulerDB db = SchedulerDB.getInstance(application);
        mTaskDao = db.getTaskDao();
    }

    @NonNull
    public TaskDao getTaskDao() {
        return mTaskDao;
    }

    @NonNull
    public LiveData<Task> getTaskById(long id) {
        if (null == mTask) {
            mTask = getTaskDao().findTaskById(id);
        }
        return mTask;
    }

    public void editTask(@NonNull final Task task) {
        new ListenableAsyncTask<Void, Void, Task>(getAsyncTaskManager(),TAG_EDIT_TASK) {
            @Override
            protected Task onExecuteInBackground(Void... voids) throws Exception {
                int changes = getTaskDao().setTask(task);
                if (1 == changes) {
                    return task;
                }
                throw new RuntimeException("unable to edit task");
            }
        }.start();
    }

    public void changeTaskState(@NonNull TaskModel model, @NonNull TaskState newState) {
        new ListenableAsyncTask<Void,Void,Task>(getAsyncTaskManager(),TAG_EDIT_TASK){
            @Override
            protected Task onExecuteInBackground(Void... voids) throws Exception {
                Task task = getTaskDao().getTaskById(model.getId());
                task.setState(newState);
                if (1 == getTaskDao().setTask(task)) {
                    return task;
                }
                throw new RuntimeException("unable to change task-state to "+newState);
            }
        }.start();
    }

    public void removeTask(@NonNull TaskModel task) {
        removeTasks(Collections.singletonList(task));
    }

    public void removeTasks(@NonNull List<TaskModel> tasks) {
        new ListenableAsyncTask<Void, Void, Boolean>(getAsyncTaskManager(),"") {
            @Override
            protected Boolean onExecuteInBackground(Void... voids) throws Exception {
                if (!tasks.isEmpty()) {
                    int count = tasks.size();
                    long[] ids = new long[count];
                    int i = 0;
                    for (TaskModel task : tasks) {
                        ids[i++] = task.getId();
                    }
                    return count == getTaskDao().removeTask(ids);
                }
                return true; // TODO: need to handle empty list condition
            }
        }.start();
    }
}
