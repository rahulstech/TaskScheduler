package rahulstech.android.taskschedulder.viewmodel;

import android.app.Application;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.model.TaskModel;

@SuppressWarnings("unused")
public class TaskReminderFragmentViewModel extends SchedulerDBViewModel {

    public static final String TAG_TIME_REMINDER_ALL_TASK = "time_reminder_all_task";

    public static final String TAG_TIME_REMINDER_CURRENT_TASK = "time_reminder_current_task";

    public static final String TAG_TIME_REMINDER_FUTURE_TASK = "time_reminder_future_task";

    private LiveData<TaskModel> mTask;

    public TaskReminderFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public LiveData<TaskModel> getTaskById(long id) {
        if (null == mTask) {
            mTask = getTaskDao().findTaskBriefById(id);
        }
        return mTask;
    }

    public  void setForCurrentTask(@NonNull Task newValues) {
        execute(TAG_TIME_REMINDER_CURRENT_TASK,()->{
            Task task = getTaskDao().getTaskById(newValues.getId());
            task.setTime(newValues.getTime());
            task.setReminder(newValues.getReminder());
            if (getTaskDao().setTask(task) == 1) {
                return task;
            }
            throw new RuntimeException("unable to set current task newValues="+newValues);
        });
    }

    public void setForAllTasks(@NonNull Task newValues) {
        execute(TAG_TIME_REMINDER_ALL_TASK,getCallableFor_setForConnectedTasks(newValues,false));
    }

    public void setForFutureTasks(@NonNull Task newValues) {
        execute(TAG_TIME_REMINDER_FUTURE_TASK,getCallableFor_setForConnectedTasks(newValues,true));
    }

    private long calculateMinutesDifferenceBetweenTaskTimeAndTaskReminderTime(@NonNull LocalDateTime taskTime, @NonNull LocalDateTime reminderTime) {
        Duration diff = Duration.between(taskTime,reminderTime);
        return diff.toMinutes();
    }

    private Callable<List<Task>> getCallableFor_setForConnectedTasks(@NonNull  Task newValues, boolean applyToFuture) {
        return ()->{
            long originTaskId = null == newValues.getOrigin() ? newValues.getId() : newValues.getOrigin();
            LocalDate after = newValues.getDate();
            LocalTime time = newValues.getTime();
            LocalDateTime reminder = newValues.getReminder();
            long diffMinutesTaskTimeReminderTime; // when this value is >= 0 then reminder is set, otherwise reminder is unset
            if (null != time && null != reminder) {
                LocalDateTime taskTime = LocalDateTime.of(after,time);
                diffMinutesTaskTimeReminderTime
                        = calculateMinutesDifferenceBetweenTaskTimeAndTaskReminderTime(taskTime,reminder);
            }
            else {
                diffMinutesTaskTimeReminderTime = -1;
            }
            List<Task> all = getTaskDao().findConnectedTasks(originTaskId);
            List<Task> tasks = new ArrayList<>();
            for (Task task : all) {
                if (applyToFuture && !after.isAfter(task.getDate())) continue;
                if (diffMinutesTaskTimeReminderTime >= 0) {
                    reminder = LocalDateTime.of(task.getDate(),time);
                }
                else {
                    reminder = null;
                }
                task.setTime(time);
                task.setReminder(reminder);
                tasks.add(task);
            }
            int changes = getTaskDao().setAllTasks(tasks);
            if (changes == tasks.size()) {
                return tasks;
            }
            throw new RuntimeException("unable to set connected tasks applyToFutureOnly="+applyToFuture+" newValues="+newValues);
        };
    }
}
