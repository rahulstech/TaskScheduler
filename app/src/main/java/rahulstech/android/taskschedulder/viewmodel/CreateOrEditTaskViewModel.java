package rahulstech.android.taskschedulder.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import rahulstech.android.database.entity.Task;

@SuppressWarnings("unused")
public class CreateOrEditTaskViewModel extends TaskViewModel {

    public static final String TAG_ADD_TASK = "addTask";

    public CreateOrEditTaskViewModel(@NonNull Application application) {
        super(application);
    }

    public void addTask(@NonNull final Task task) {
        execute(TAG_ADD_TASK,()->{
            long id = getTaskDao().addTask(task);
            if (id > 0) {
                task.setId(id);
                return task;
            }
            throw new RuntimeException("unable to add task="+task+" id-returned="+id);
        });
    }
}
