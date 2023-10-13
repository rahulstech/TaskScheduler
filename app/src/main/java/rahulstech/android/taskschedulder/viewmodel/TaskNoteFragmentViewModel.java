package rahulstech.android.taskschedulder.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.database.entity.Task;

@SuppressWarnings("unused")
public class TaskNoteFragmentViewModel extends SchedulerDBViewModel {

    public static final String TAG_SAVE_NOTE = "tag_save_note";

    private LiveData<Task> mTask;

    public TaskNoteFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public LiveData<Task> getTaskById(long id) {
        if (null == mTask) {
            mTask = getTaskDao().findTaskById(id);
        }
        return mTask;
    }

    public void saveNote(@NonNull Task newValue) {
        execute(TAG_SAVE_NOTE,()->{
            Task task = getTaskDao().getTaskById(newValue.getId());
            task.setNote(newValue.getNote());
            if (1 == getTaskDao().setTask(task)) {
                return task;
            }
            throw new RuntimeException("unable to save note newValue="+newValue);
        });
    }
}
