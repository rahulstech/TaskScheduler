package rahulstech.android.database.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.RoomWarnings;
import rahulstech.android.database.datatype.TaskRepeat;
import rahulstech.android.database.datatype.TaskState;
import rahulstech.android.database.datatype.TimeUnit;

@SuppressWarnings(value = {"unused", RoomWarnings.CURSOR_MISMATCH})
public class TaskModel {

    private Long id;

    private TaskState state;

    private String description;

    private LocalDate date;

    private LocalTime time;

    private LocalDateTime reminder;

    private Long origin;

    private String note;

    private Integer connections;

    private LocalDate rangeStart;

    private LocalDate rangeEnd;

    private TaskRepeat frequency;

    private Integer interval;

    private TimeUnit unit;

    public TaskModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public Integer getConnections() {
        return connections;
    }

    public void setConnections(Integer connections) {
        this.connections = connections;
    }

    public LocalDate getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(LocalDate rangeStart) {
        this.rangeStart = rangeStart;
    }

    public LocalDate getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(LocalDate rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public TaskRepeat getFrequency() {
        return frequency;
    }

    public void setFrequency(TaskRepeat frequency) {
        this.frequency = frequency;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskModel{" +
                "id=" + id +
                ", state=" + state +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", reminder=" + reminder +
                ", origin=" + origin +
                ", note='" + note + '\'' +
                ", connections=" + connections +
                ", rangeStart=" + rangeStart +
                ", rangeEnd=" + rangeEnd +
                ", frequency=" + frequency +
                ", interval=" + interval +
                ", unit=" + unit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskModel)) return false;
        TaskModel taskModel = (TaskModel) o;
        return Objects.equals(id, taskModel.id) && state == taskModel.state && Objects.equals(description, taskModel.description) && Objects.equals(date, taskModel.date) && Objects.equals(time, taskModel.time) && Objects.equals(reminder, taskModel.reminder) && Objects.equals(origin, taskModel.origin) && Objects.equals(note, taskModel.note) && Objects.equals(connections, taskModel.connections) && Objects.equals(rangeStart, taskModel.rangeStart) && Objects.equals(rangeEnd, taskModel.rangeEnd) && frequency == taskModel.frequency && Objects.equals(interval, taskModel.interval) && unit == taskModel.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, description, date, time, reminder, origin, note, connections, rangeStart, rangeEnd, frequency, interval, unit);
    }

    @NonNull
    public TaskModel copy() {
        TaskModel copy = new TaskModel();
        copy.id = id;
        copy.description = description;
        copy.state = state;
        copy.date = date;
        copy.time = time;
        copy.reminder = reminder;
        copy.origin = origin;
        copy.note = note;
        copy.connections = connections;
        copy.rangeStart = rangeStart;
        copy.rangeEnd = rangeEnd;
        copy.frequency = frequency;
        copy.interval = interval;
        copy.unit = unit;
        return copy;
    }
}
