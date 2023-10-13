package rahulstech.android.database.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.datatype.DatatypeConverters;
import rahulstech.android.database.datatype.TaskState;

@Entity(tableName = "tasks")
@SuppressWarnings(value = {"unused","deprecation"})
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private TaskState state;

    @NonNull
    private String description;

    @NonNull
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private LocalDate date;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private LocalTime time;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private LocalDateTime reminder;

    private Long origin;

    private String note;

    public Task() {
        setState(TaskState.PENDING);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    @Deprecated
    public DBDate getDateStart() {
        return DBDate.of(date.getYear(),date.getMonthValue(),date.getDayOfMonth());
    }

    @Deprecated
    public void setDateStart(@NonNull DBDate dateStart) {
        this.date = LocalDate.of(dateStart.getYear(),dateStart.getMonth(),dateStart.getDate());
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

    @Deprecated
    public DBTime getTimeStart() {
        if (null == time) return null;
        return DBTime.of(time.getHour(),time.getMinute());
    }

    @Deprecated
    public void setTimeStart(DBTime timeStart) {
        if (timeStart == null) {
            this.time = null;
            return;
        }
        this.time = LocalTime.of(timeStart.getHourOfDay(),timeStart.getMinute());
    }

    @NonNull
    public LocalDate getDate() {
        return date;
    }

    public void setDate(@NonNull LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDateTime getReminder() {
        return reminder;
    }

    public void setReminder(LocalDateTime reminder) {
        this.reminder = reminder;
    }

    public Long getOrigin() {
        return origin;
    }

    public void setOrigin(Long origin) {
        this.origin = origin;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", state=" + state +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", reminder=" + reminder +
                ", origin=" + origin +
                ", note='" + note + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id && state == task.state && description.equals(task.description) && date.equals(task.date) && Objects.equals(time, task.time) && Objects.equals(reminder, task.reminder) && Objects.equals(origin, task.origin) && Objects.equals(note, task.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, description, date, time, reminder, origin, note);
    }

    @NonNull
    public Task copy() {
        Task copy = new Task();
        copy.id = id;
        copy.description = description;
        copy.state = state;
        copy.date = date;
        copy.time = time;
        copy.reminder = reminder;
        copy.origin = origin;
        copy.note = note;
        return copy;
    }
}
