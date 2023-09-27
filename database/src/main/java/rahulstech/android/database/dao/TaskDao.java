package rahulstech.android.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.database.model.TaskWithTaskDataModel;

@Dao
@SuppressWarnings("unused")
public interface TaskDao {

    @Insert
    long addTask(Task newTask);

    @Update
    int setTask(Task task);

    @Delete
    int removeTask( Task task);

    @Query("SELECT `id`,`description`,`state`,`dateStart` FROM `tasks` WHERE `dateStart` = :dateStart")
    LiveData<List<TaskModel>> findTaskForDate(DBDate dateStart);

    @Query("SELECT * FROM `tasks` WHERE `id` = :id")
    LiveData<TaskModel> findTaskById(long id);

    @Transaction
    @Query("SELECT * FROM `tasks` WHERE `id` = :taskId")
    LiveData<TaskWithTaskDataModel> findLiveTaskWithTaskDataModelByTaskId(long taskId);
}
