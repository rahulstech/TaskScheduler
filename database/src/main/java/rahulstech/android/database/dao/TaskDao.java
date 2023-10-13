package rahulstech.android.database.dao;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskModel;

@Dao
@SuppressWarnings("unused")
public abstract class TaskDao {

    @Transaction
    @Insert
    public abstract long[] addAllTasks(@NonNull List<Task> tasks);

    @Insert
    public abstract long addTask(@NonNull Task newTask);

    @Update
    public abstract int setTask(@NonNull Task task);

    @Update
    public abstract int setAllTasks(@NonNull List<Task> tasks);

    @Query("SELECT * FROM `tasks` WHERE `id` = :id")
    public abstract Task getTaskById(long id);

    @Insert
    protected abstract long insert_task_data(TaskData data);

    @Query("DELETE FROM `tasks` WHERE `id` IN(:ids)")
    @Deprecated
    public abstract int removeTask(long[] ids);

    @Query("DELETE FROM `tasks` WHERE `id` IN(:ids)")
    public abstract int removeTasks(@NonNull List<Long> ids);

    @Query("SELECT `id`,`description`,`state`,`date`,`time` FROM `tasks` WHERE `date` = :date")
    public abstract LiveData<List<TaskModel>> findTasksForDate(@NonNull LocalDate date);

    @Query("SELECT * FROM `tasks` WHERE `id` = :origin OR `origin` = :origin")
    public abstract List<Task> findConnectedTasks(long origin);

    @Query("SELECT * FROM `tasks` WHERE `id` = :id")
    public abstract LiveData<Task> findTaskById(long id);

    @Query("SELECT * FROM `tasks_brief` WHERE `id` = :id")
    public abstract LiveData<TaskModel> findTaskBriefById(long id);
}
