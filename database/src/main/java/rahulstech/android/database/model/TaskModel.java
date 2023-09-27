package rahulstech.android.database.model;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.RoomWarnings;
import androidx.room.TypeConverters;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DatatypeConverters;
import rahulstech.android.database.datatype.TaskState;

@SuppressWarnings(value = {"unused", RoomWarnings.CURSOR_MISMATCH})
public class TaskModel {

    private long id;

    @TypeConverters(DatatypeConverters.class)
    private DBDate dateStart;

    private TaskState state;

    private String description;

    public TaskModel() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @NonNull
    @Override
    public String toString() {
        return "TaskModel{" +
                "id=" + id +
                ", dateStart=" + dateStart +
                ", state=" + state +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskModel)) return false;
        TaskModel taskModel = (TaskModel) o;
        return id == taskModel.id && Objects.equals(dateStart, taskModel.dateStart) && state == taskModel.state && Objects.equals(description, taskModel.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateStart, state, description);
    }
}
