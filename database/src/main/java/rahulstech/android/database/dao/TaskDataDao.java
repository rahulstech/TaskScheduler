package rahulstech.android.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskWithTaskDataModel;

@Dao
public interface TaskDataDao {

    @Transaction
    @Insert
    long[] insertTaskData( List<TaskData> data);

    @Transaction
    @Query("SELECT * FROM `tasks` WHERE `id` = :taskId")
    public abstract LiveData<TaskWithTaskDataModel> findLiveTaskWithTaskDataModelByTaskId(long taskId);

    @Transaction
    @Update
    int updateTaskData( List<TaskData> data);

    @Transaction
    @Delete
    int deleteTaskData(List<TaskData> data);
}
