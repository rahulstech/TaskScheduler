package rahulstech.android.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.database.model.TaskWithTaskDataModel;

public class ViewTaskViewModel extends TaskDataViewModel {

    private LiveData<TaskWithTaskDataModel> mTaskWithData;

    public ViewTaskViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public LiveData<TaskWithTaskDataModel> getTaskWithDataByTaskId(long id) {
        if (null == mTaskWithData) {
            mTaskWithData = getTaskDataDao().findLiveTaskWithTaskDataModelByTaskId(id);
        }
        return mTaskWithData;
    }
}
