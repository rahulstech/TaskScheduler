package rahulstech.android.taskschedulder.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.database.model.TaskModel;

@SuppressWarnings("unused")
public class CopyTaskFragmentViewModel extends SchedulerDBViewModel {

    private LiveData<TaskModel> mTask;

    public CopyTaskFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public LiveData<TaskModel> getTaskById(long id) {
        if (null == mTask) {
            mTask = getTaskDao().findTaskBriefById(id);
        }
        return mTask;
    }
}
