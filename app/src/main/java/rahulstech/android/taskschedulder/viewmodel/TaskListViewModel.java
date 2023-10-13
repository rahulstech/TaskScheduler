package rahulstech.android.taskschedulder.viewmodel;

import android.app.Application;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.util.time.DateTime;

@SuppressWarnings("unused")
public class TaskListViewModel extends TaskViewModel {

    private final LiveData<List<TaskModel>> mTasks;
    private final MutableLiveData<LocalDate> mTaskDates = new MutableLiveData<>();

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        mTasks = Transformations.switchMap(mTaskDates, date -> getTaskDao().findTasksForDate(date));
    }

    @NonNull
    public LiveData<List<TaskModel>> getAllTasksForDate() {
        return mTasks;
    }

    @Deprecated
    public void changeTaskDate(@NonNull DateTime date) {
        changeTaskDate(LocalDate.of(date.getYear(),date.getMonth(),date.getDate()));
    }

    public void changeTaskDate(@NonNull LocalDate date) {
        mTaskDates.setValue(date);
    }
}
