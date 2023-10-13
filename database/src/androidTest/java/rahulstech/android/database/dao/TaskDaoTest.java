package rahulstech.android.database.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.TaskState;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.database.util.FakeDataProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static rahulstech.android.database.util.DBUtils.createInMemorySchedulerDB;
import static rahulstech.android.database.util.DBUtils.testLiveData;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {

    SchedulerDB db;
    TaskDao dao;

    @Before
    public void setUp() throws Exception {
        db = createInMemorySchedulerDB(ApplicationProvider.getApplicationContext(), new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                FakeDataProvider.getForDBVersion(1).insert(db);
            }
        });
        dao = db.getTaskDao();
    }

    @After
    public void tearDown() throws Exception {
        db.close();
    }

    @Test
    public void addTask() {
        Task task = new Task();
        task.setDescription("This is new task");
        task.setDateStart(DBDate.of(2023,5,6));
        task.setState(TaskState.PENDING);
        long id = dao.addTask(task);
        assertTrue(id > 0);
    }

    @Test
    public void setTask() {
        // db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
        //                "VALUES (3,\"Task 3\",\"2023-04-08\",\"PAUSE\");");
        Task task = new Task();
        task.setId(3);
        task.setDescription("Task 3");
        task.setDateStart(DBDate.of(2023,3,8));
        task.setState(TaskState.CANCEL);

        int actual = dao.setTask(task);
        assertEquals(1,actual);
    }

    @Test
    public void removeTask() {
        int actual = dao.removeTask(new long[]{1,2});
        assertEquals(2,actual);
    }

    @Test
    public void findAllForDate() throws Exception {
        LiveData<List<TaskModel>> tasksLiveData = dao.findTasksForDate(LocalDate.of(2023,3,8));
        testLiveData(tasksLiveData,actual -> {
            /**
             * db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
             *                 "VALUES (3,\"Task 3\",\"2023-04-08\",\"PAUSE\");");
             *
             *  db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
             *                 "VALUES (5,\"Task 5\",\"2023-04-08\",\"START\");");
             */
            TaskModel task3 = new TaskModel();
            task3.setId(3L);
            task3.setDescription("Task 3");
            task3.setDate(LocalDate.of(2023,3,8));
            task3.setState(TaskState.PAUSE);

            TaskModel task5 = new TaskModel();
            task5.setId(5L);
            task5.setDescription("Task 5");
            task5.setDate(LocalDate.of(2023,3,8));
            task5.setState(TaskState.START);

            List<TaskModel> expected = Arrays.asList(task3,task5);

            assertEquals(expected,actual);

        });
    }

    @Test
    public void findTaskById() throws Exception {
        LiveData<Task> liveData = dao.findTaskById(3);
        testLiveData(liveData, actual -> {
            // db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
            //                "VALUES (3,\"Task 3\",\"2023-04-08\",\"PAUSE\");");
            Task expected = new Task();
            expected.setId(3);
            expected.setDescription("Task 3");
            expected.setDateStart(DBDate.of(2023,3,8));
            expected.setState(TaskState.PAUSE);

            assertEquals(expected,actual);
        });
    }
}
