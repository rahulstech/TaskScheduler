package rahulstech.android.taskschedulder.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import rahulstech.android.util.concurrent.AsyncTasksManager;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

@SuppressWarnings("unused")
public class BaseViewModel extends AndroidViewModel {

    private final AsyncTasksManager mTaskManager = new AsyncTasksManager();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public AsyncTasksManager getAsyncTaskManager() {
        return mTaskManager;
    }

    @SuppressLint("StaticFieldLeak")
    public <T,R> void execute(@NonNull String tag, @NonNull Callable<R> callable) {
        new ListenableAsyncTask<T,Void,R>(getAsyncTaskManager(),tag) {
            @Override
            protected R onExecuteInBackground(T[] voids) throws Exception {
                return callable.call();
            }
        }.start();
    }
}
