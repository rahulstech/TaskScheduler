package rahulstech.android.util.concurrent;

import android.os.AsyncTask;

import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class AsyncTasksManager {

    private final ConcurrentHashMap<Integer,AsyncTask<?,?,?>> mTaskMap = new ConcurrentHashMap<>();

    public AsyncTasksManager() {}

    public void addAsyncTask(int id, @NonNull AsyncTask<?,?,?> task) {
        AsyncTask<?,?,?> old = mTaskMap.get(id);
        if (null != old && old.getStatus() != AsyncTask.Status.FINISHED) {
            throw new IllegalStateException("can not add new task with id="+id+", another unfinished task already exists");
        }
        mTaskMap.put(id,task);
    }

    public void removeTask(int id) {
        mTaskMap.remove(id);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends AsyncTask<?,?,?>> T getTask(int id) {
        AsyncTask<?,?,?> task = mTaskMap.get(id);
        return (T) task;
    }
}
