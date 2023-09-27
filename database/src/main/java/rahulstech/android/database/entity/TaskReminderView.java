package rahulstech.android.database.entity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.DatabaseView;
import androidx.room.TypeConverters;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.datatype.DatatypeConverters;
import rahulstech.android.database.datatype.TaskState;

@DatabaseView(viewName = "view_task_reminder", value = "SELECT`tasks`.`state` AS `state`, `tasks`.`description` AS `description`, `tasks`.`id` AS `taskId`, `task_data`.`id` AS `dataId`," +
        " `task_data`.`date1` AS `taskDate` , `task_data`.`time1` AS `taskTime`, " +
        " `task_data`.`date2` AS `reminderDate` , `task_data`.`time2` AS `reminderTime` " +
        " FROM `task_data` INNER JOIN `tasks` ON `task_data`.`taskId` = `tasks`.`id` " +
        " WHERE `tasks`.`dateStart` >= DATETIME('now') AND `tasks`.`state` NOT IN(\"CANCEL\",\"COMPLETE\")")
@SuppressWarnings("unused")
public class TaskReminderView {

    private long taskId;

    private long dataId;

    private String description;

    private TaskState state;

    @TypeConverters(DatatypeConverters.class)
    private DBDate taskDate;

    @TypeConverters(DatatypeConverters.class)
    private DBTime taskTime;

    @TypeConverters(DatatypeConverters.class)
    private DBDate reminderData;

    @TypeConverters(DatatypeConverters.class)
    private DBTime reminderTime;

    public TaskReminderView() {}

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public DBDate getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(DBDate taskDate) {
        this.taskDate = taskDate;
    }

    public DBTime getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(DBTime taskTime) {
        this.taskTime = taskTime;
    }

    public DBDate getReminderData() {
        return reminderData;
    }

    public void setReminderData(DBDate reminderData) {
        this.reminderData = reminderData;
    }

    public DBTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(DBTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskReminderView{" +
                "taskId=" + taskId +
                ", dataId=" + dataId +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", taskDate=" + taskDate +
                ", taskTime=" + taskTime +
                ", reminderData=" + reminderData +
                ", reminderTime=" + reminderTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskReminderView)) return false;
        TaskReminderView that = (TaskReminderView) o;
        return taskId == that.taskId && dataId == that.dataId && Objects.equals(description, that.description) && state == that.state && Objects.equals(taskDate, that.taskDate) && Objects.equals(taskTime, that.taskTime) && Objects.equals(reminderData, that.reminderData) && Objects.equals(reminderTime, that.reminderTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, dataId, description, state, taskDate, taskTime, reminderData, reminderTime);
    }
}
