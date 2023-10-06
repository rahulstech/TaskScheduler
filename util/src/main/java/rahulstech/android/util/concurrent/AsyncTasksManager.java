package rahulstech.android.util.concurrent;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

@SuppressWarnings("unused")
public class AsyncTasksManager {

    private final Map<String,ListenableAsyncTask<?,?,?>> mTaskMap = new HashMap<>();

    private final MutableLiveData<ListenableAsyncTask<?,?,?>> mTaskUpdateLiveData = new MutableLiveData<>();

    public AsyncTasksManager() {}

    @NonNull
    public LiveData<ListenableAsyncTask<?,?,?>> getTaskUpdateLiveData() {
        return mTaskUpdateLiveData;
    }

    public void addTask(@NonNull String tag, @NonNull ListenableAsyncTask<?,?,?> task) {
        synchronized (mTaskMap) {
            AsyncTask<?, ?, ?> old = mTaskMap.get(tag);
            if (null != old && old.getStatus() != AsyncTask.Status.FINISHED) {
                throw new IllegalStateException("can not add new task with tag=" + tag + ", another unfinished task already exists");
            }
            mTaskMap.put(tag, task);
        }
    }

    public void removeTask(@NonNull String tag) {
        synchronized (mTaskMap) {
            mTaskMap.remove(tag);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends ListenableAsyncTask<?,?,?>> T getTask(@NonNull String tag) {
        synchronized (mTaskMap) {
            Object task = mTaskMap.get(tag);
            return (T) task;
        }
    }
}
