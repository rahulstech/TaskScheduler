package rahulstech.android.ui.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.util.time.DateTime;

@SuppressWarnings("unused")
public class TaskListViewModel extends TaskViewModel {

    private final LiveData<List<TaskModel>> mTasks;
    private final MutableLiveData<DateTime> mTaskDates = new MutableLiveData<>();

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        mTasks = Transformations.switchMap(mTaskDates, date -> getTaskDao().findTaskForDate(DBDate.of(date.getYear(),date.getMonth(),date.getDate())));
    }

    @NonNull
    public LiveData<List<TaskModel>> getAllTasksForDate() {
        return mTasks;
    }

    public void changeTaskDate(@NonNull DateTime date) {
        mTaskDates.setValue(date);
    }
}
