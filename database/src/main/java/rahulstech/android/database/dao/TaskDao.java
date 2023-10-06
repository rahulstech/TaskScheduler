package rahulstech.android.database.dao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.datatype.TaskDataType;
import rahulstech.android.database.datatype.TaskState;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskModel;

@Dao
@SuppressWarnings("unused")
public abstract class TaskDao {

    @Transaction
    public long addTask(@NonNull Task newTask) {
        long id = insert_task(newTask);
        if (id > 0) {
            TaskData data = new TaskData();
            data.setTaskId(id);
            data.setType(TaskDataType.TYPE_TASK_STATE);
            data.setDate1(DBDate.today());
            data.setTime1(DBTime.now());
            data.setText5(newTask.getState().name());
            if (insert_task_data(data) > 0){
                return id;
            }
        }
        return 0;
    }

    @Insert
    protected abstract long insert_task(Task newTask);

    @Transaction
    public int setTask(Task task) {
        Task oldTask = getTaskById(task.getId());
        if (1 == update_task(task)) {
            TaskState oldState = oldTask.getState();
            TaskState newState = task.getState();
            if (!oldState.equals(newState)) {
                TaskData data = new TaskData();
                data.setTaskId(task.getId());
                data.setType(TaskDataType.TYPE_TASK_STATE);
                data.setText5(newState.name());
                data.setDate1(DBDate.today());
                data.setTime1(DBTime.now());
                if (insert_task_data(data)>0) {
                    return 1;
                }
            }
            return 1;
        }
        return 0;
    }

    @Update
    protected abstract int update_task(Task task);

    @Query("SELECT * FROM `tasks` WHERE `id` = :id")
    public abstract Task getTaskById(long id);

    @Insert
    protected abstract long insert_task_data(TaskData data);

    @Query("DELETE FROM `tasks` WHERE `id` IN(:ids)")
    public abstract int removeTask(long[] ids);

    @Query("SELECT * FROM `tasks` WHERE `dateStart` = :dateStart")
    public abstract LiveData<List<TaskModel>> findTaskForDate(DBDate dateStart);

    // TODO: change return type of this method
    @Query("SELECT * FROM `tasks` WHERE `id` = :id")
    public abstract LiveData<Task> findTaskById(long id);
}
