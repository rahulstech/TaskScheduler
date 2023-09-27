package rahulstech.android.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Transaction;
import androidx.room.Update;
import rahulstech.android.database.entity.TaskData;

@Dao
public interface TaskDataDao {

    @Transaction
    @Insert
    long[] insertTaskData( List<TaskData> data);

    @Transaction
    @Update
    int updateTaskData( List<TaskData> data);

    @Transaction
    @Delete
    int deleteTaskData(List<TaskData> data);


}
