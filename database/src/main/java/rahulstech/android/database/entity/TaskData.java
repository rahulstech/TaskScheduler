package rahulstech.android.database.entity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.datatype.DatatypeConverters;

@Entity(tableName = "task_data",
        foreignKeys = {
        @ForeignKey(entity = Task.class, parentColumns = {"id"},childColumns = {"taskId"}, onDelete = ForeignKey.CASCADE)
})
@SuppressWarnings("unused")
public class TaskData {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long taskId;

    @NonNull
    private String type;

    private String text1;

    private String text2;

    private String text3;

    private String text4;

    private String text5;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private DBDate date1;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private DBDate date2;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private DBTime time1;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @TypeConverters(DatatypeConverters.class)
    private DBTime time2;

    private Long long1;

    private Long long2;

    private Double double1;

    private Double double2;

    private Boolean boolean1;

    private Boolean boolean2;

    public TaskData() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getText4() {
        return text4;
    }

    public void setText4(String text4) {
        this.text4 = text4;
    }

    public String getText5() {
        return text5;
    }

    public void setText5(String text5) {
        this.text5 = text5;
    }

    public DBDate getDate1() {
        return date1;
    }

    public void setDate1(DBDate date1) {
        this.date1 = date1;
    }

    public DBDate getDate2() {
        return date2;
    }

    public void setDate2(DBDate date2) {
        this.date2 = date2;
    }

    public DBTime getTime1() {
        return time1;
    }

    public void setTime1(DBTime time1) {
        this.time1 = time1;
    }

    public DBTime getTime2() {
        return time2;
    }

    public void setTime2(DBTime time2) {
        this.time2 = time2;
    }

    public Long getLong1() {
        return long1;
    }

    public void setLong1(Long long1) {
        this.long1 = long1;
    }

    public Long getLong2() {
        return long2;
    }

    public void setLong2(Long long2) {
        this.long2 = long2;
    }

    public Double getDouble1() {
        return double1;
    }

    public void setDouble1(Double double1) {
        this.double1 = double1;
    }

    public Double getDouble2() {
        return double2;
    }

    public void setDouble2(Double double2) {
        this.double2 = double2;
    }

    public Boolean getBoolean1() {
        return boolean1;
    }

    public void setBoolean1(Boolean boolean1) {
        this.boolean1 = boolean1;
    }

    public Boolean getBoolean2() {
        return boolean2;
    }

    public void setBoolean2(Boolean boolean2) {
        this.boolean2 = boolean2;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskData{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", type='" + type + '\'' +
                ", text1='" + text1 + '\'' +
                ", text2='" + text2 + '\'' +
                ", text3='" + text3 + '\'' +
                ", text4='" + text4 + '\'' +
                ", text5='" + text5 + '\'' +
                ", date1=" + date1 +
                ", date2=" + date2 +
                ", time1=" + time1 +
                ", time2=" + time2 +
                ", long1=" + long1 +
                ", long2=" + long2 +
                ", double1=" + double1 +
                ", double2=" + double2 +
                ", boolean1=" + boolean1 +
                ", boolean2=" + boolean2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskData)) return false;
        TaskData taskData = (TaskData) o;
        return id == taskData.id && taskId == taskData.taskId && Objects.equals(type, taskData.type) && Objects.equals(text1, taskData.text1) && Objects.equals(text2, taskData.text2) && Objects.equals(text3, taskData.text3) && Objects.equals(text4, taskData.text4) && Objects.equals(text5, taskData.text5) && Objects.equals(date1, taskData.date1) && Objects.equals(date2, taskData.date2) && Objects.equals(time1, taskData.time1) && Objects.equals(time2, taskData.time2) && Objects.equals(long1, taskData.long1) && Objects.equals(long2, taskData.long2) && Objects.equals(double1, taskData.double1) && Objects.equals(double2, taskData.double2) && Objects.equals(boolean1, taskData.boolean1) && Objects.equals(boolean2, taskData.boolean2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskId, type, text1, text2, text3, text4, text5, date1, date2, time1, time2, long1, long2, double1, double2, boolean1, boolean2);
    }

    @NonNull
    public TaskData copy() {
        TaskData copy = new TaskData();
        copy.id = id;
        copy.taskId = taskId;
        copy.type = type;
        copy.text1 = text1;
        copy.text2 = text2;
        copy.text3 = text3;
        copy.text4 = text4;
        copy.text5 = text5;
        copy.date1 = date1;
        copy.date2 = date2;
        copy.time1 = time1;
        copy.time2 = time2;
        copy.long1 = long1;
        copy.long2 = long2;
        copy.double1 = double1;
        copy.double2 = double2;
        copy.boolean1 = boolean1;
        copy.boolean2 = boolean2;
        return copy;
    }
}
