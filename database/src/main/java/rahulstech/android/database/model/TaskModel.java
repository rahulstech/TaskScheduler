package rahulstech.android.database.model;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.RoomWarnings;
import androidx.room.TypeConverters;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.datatype.DatatypeConverters;
import rahulstech.android.database.datatype.TaskState;

@SuppressWarnings(value = {"unused", RoomWarnings.CURSOR_MISMATCH})
public class TaskModel {

    private Long id;

    @TypeConverters(DatatypeConverters.class)
    private DBDate dateStart;

    private TaskState state;

    private String description;

    private DBTime timeStart;

    public TaskModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(DBDate dateStart) {
        this.dateStart = dateStart;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DBTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(DBTime timeStart) {
        this.timeStart = timeStart;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskModel{" +
                "id=" + id +
                ", dateStart=" + dateStart +
                ", state=" + state +
                ", description='" + description + '\'' +
                ", timeStart=" + timeStart +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskModel)) return false;
        TaskModel taskModel = (TaskModel) o;
        return Objects.equals(id, taskModel.id) && Objects.equals(dateStart, taskModel.dateStart) && state == taskModel.state && Objects.equals(description, taskModel.description) && Objects.equals(timeStart, taskModel.timeStart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateStart, state, description, timeStart);
    }

    @NonNull
    public TaskModel copy() {
        TaskModel copy = new TaskModel();
        copy.id = id;
        copy.description = description;
        copy.dateStart = dateStart;
        copy.state = state;
        copy.timeStart = timeStart;
        return copy;
    }
}
