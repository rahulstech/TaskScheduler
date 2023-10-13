package rahulstech.android.database.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.DatabaseView;
import rahulstech.android.database.datatype.TaskRepeat;
import rahulstech.android.database.datatype.TimeUnit;

@DatabaseView(viewName = "tasks_brief",
        value = "SELECT t1.*," +
                "       t2.connections,t2.rangeStart,t2.rangeEnd," +
                "       t3.text4 frequency," +
                "       t3.text5 unit," +
                "       t3.long1 interval" +
                "  FROM tasks t1" +
                "       LEFT JOIN" +
                "       (" +
                "           SELECT id, CASE WHEN origin IS NOT NULL THEN origin ELSE id END origin," +
                "                  count(id) connections," +
                "                  min(date(date) ) rangeStart," +
                "                  max(date(date) ) rangeEnd" +
                "             FROM tasks" +
                "            GROUP BY CASE WHEN origin IS NOT NULL THEN origin ELSE id END" +
                "       )" +
                "       t2 ON t1.id = t2.origin OR " +
                "             t1.origin = t2.origin" +
                "       LEFT JOIN" +
                "       task_data t3 ON type IN(\"task_frequency\") AND " +
                "                       taskId = t2.origin;")
@SuppressWarnings("unused")
public class TaskBriefView {

    private long id;

    private String description;

    private LocalDate date;

    private LocalTime time;

    private LocalDateTime reminder;

    private String note;

    private Long origin;

    private int connections;

    private LocalDate rangeStart;

    private LocalDate rangeEnd;

    private TaskRepeat frequency;

    private Integer interval;

    private TimeUnit unit;

    public TaskBriefView() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getOrigin() {
        return origin;
    }

    public void setOrigin(Long origin) {
        this.origin = origin;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
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
        return "TaskBriefView{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", reminder=" + reminder +
                ", note='" + note + '\'' +
                ", origin=" + origin +
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
        if (!(o instanceof TaskBriefView)) return false;
        TaskBriefView that = (TaskBriefView) o;
        return id == that.id && connections == that.connections && Objects.equals(description, that.description) && Objects.equals(date, that.date) && Objects.equals(time, that.time) && Objects.equals(reminder, that.reminder) && Objects.equals(note, that.note) && Objects.equals(origin, that.origin) && Objects.equals(rangeStart, that.rangeStart) && Objects.equals(rangeEnd, that.rangeEnd) && frequency == that.frequency && Objects.equals(interval, that.interval) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, date, time, reminder, note, origin, connections, rangeStart, rangeEnd, frequency, interval, unit);
    }
}
