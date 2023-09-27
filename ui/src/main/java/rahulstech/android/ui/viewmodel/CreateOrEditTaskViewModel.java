package rahulstech.android.ui.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

@SuppressWarnings("unused")
public class CreateOrEditTaskViewModel extends TaskViewModel {

    private LiveData<TaskModel> mTask = null;

    public CreateOrEditTaskViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public LiveData<TaskModel> getTaskById(long id) {
        if (null == mTask) {
            mTask = getTaskDao().findTaskById(id);
        }
        return mTask;
    }

    @SuppressLint("StaticFieldLeak")
    public ListenableAsyncTask<Void,Void, Task> addTask(@NonNull final Task task) {
        return new ListenableAsyncTask<Void, Void, Task>(getAsyncTaskManager()) {
            @Override
            protected Task onExecuteInBackground(Void... voids) throws Exception {
                long id = getTaskDao().addTask(task);
                if (id > 0) {
                    task.setId(id);
                    return task;
                }
                throw new RuntimeException("unable to add task");
            }
        };
    }
}
