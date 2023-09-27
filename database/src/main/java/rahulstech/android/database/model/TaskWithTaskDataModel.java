package rahulstech.android.database.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;
import rahulstech.android.database.entity.TaskData;

@SuppressWarnings("unused")
public class TaskWithTaskDataModel {

    @Embedded
    private TaskModel task;

    @Relation(entity = TaskData.class, parentColumn = "id", entityColumn = "taskId")
    private List<TaskData> data;

    @Ignore
    private HashMap<String,List<TaskData>> mTypeDataMap = new HashMap<>();

    public TaskWithTaskDataModel() {}

    public long getTaskId() {
        return task.getId();
    }

    public TaskModel getTask() {
        return task;
    }

    public void setTask(TaskModel task) {
        this.task = task;
    }

    public List<TaskData> getData() {
        return data;
    }

    public void setData(List<TaskData> data) {
        this.data = data;
        if (null != data) {
            createDataTypeTaskDataMap(data);
        }
    }

    @NonNull
    public List<TaskData> getDataForType(@NonNull String type) {
        List<TaskData> data = mTypeDataMap.get(type);
        if (null == data) return Collections.emptyList();
        return data;
    }

    @Nullable
    public TaskData getSingleDataForType(@NonNull String type) {
        List<TaskData> data = getDataForType(type);
        if (!data.isEmpty()) return data.get(0);
        return null;
    }

    private void createDataTypeTaskDataMap(@NonNull List<TaskData> data) {
        for (TaskData d : data) {
            List<TaskData> existing = mTypeDataMap.get(d.getType());
            if (null == existing) {
                existing = new ArrayList<>();
                mTypeDataMap.put(d.getType(),existing);
            }
            existing.add(d);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskWithTaskDataModel{" +
                "task=" + task +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskWithTaskDataModel)) return false;
        TaskWithTaskDataModel that = (TaskWithTaskDataModel) o;
        return Objects.equals(task, that.task) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, data);
    }
}
