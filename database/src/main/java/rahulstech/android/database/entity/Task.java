package rahulstech.android.database.entity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DatatypeConverters;
import rahulstech.android.database.datatype.TaskState;

@Entity(tableName = "tasks")
@SuppressWarnings(value = {"unused"})
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private DBDate dateStart;

    @NonNull
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    private TaskState state;

    @NonNull
    private String description;

    public Task() {
        this.dateStart = DBDate.today();
        this.state = TaskState.CREATE;
        this.description = "No Description";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public DBDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(@NonNull DBDate dateStart) {
        this.dateStart = dateStart;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public TaskState getState() {
        return state;
    }

    public void setState(@NonNull TaskState state) {
        this.state = state;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", dateStart=" + dateStart +
                ", state=" + state +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id && dateStart.equals(task.dateStart) && state == task.state && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateStart, state, description);
    }

    @NonNull
    public Task copy() {
        Task copy = new Task();
        copy.id = this.id;
        copy.dateStart = this.dateStart;
        copy.state = this.state;
        copy.description = this.description;
        return copy;
    }
}
