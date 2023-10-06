package rahulstech.android.ui.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import rahulstech.android.database.entity.Task;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

@SuppressWarnings("unused")
public class CreateOrEditTaskViewModel extends TaskViewModel {

    public static final String TAG_ADD_TASK = "addTask";

    public CreateOrEditTaskViewModel(@NonNull Application application) {
        super(application);
    }

    @SuppressLint("StaticFieldLeak")
    public void addTask(@NonNull final Task task) {
        new ListenableAsyncTask<Void, Void, Task>(getAsyncTaskManager(),TAG_ADD_TASK) {
            @Override
            protected Task onExecuteInBackground(Void... voids) throws Exception {
                long id = getTaskDao().addTask(task);
                if (id > 0) {
                    task.setId(id);
                    return task;
                }
                throw new RuntimeException("unable to add task="+task+" id-returned="+id);
            }
        }.start();
    }
}
