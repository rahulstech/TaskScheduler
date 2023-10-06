package rahulstech.android.util.concurrent;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

@SuppressWarnings("unused")
public abstract class ListenableAsyncTask<Param,Progress,Result> extends AsyncTask<Param,Progress, ListenableAsyncTask.TaskResult> {

    private static final String TAG = "ListenableAsyncTask";

    private final AsyncTasksManager mManager;
    private final String mTag;

    private int mVersion = 0;
    private Progress[] mProgress = null;

    public ListenableAsyncTask(AsyncTasksManager manager, String tag) {
        Objects.requireNonNull(manager,"null == AsyncTaskManager");
        Objects.requireNonNull(tag,"null == tag");
        this.mManager = manager;
        this.mTag = tag;
    }

    @NonNull
    public final String getTag(){
        return mTag;
    }

    @SafeVarargs
    public final void start(Param... params) {
        mManager.addTask(mTag,this);
        execute(params);
    }

    @SafeVarargs
    @Override
    protected final TaskResult doInBackground(Param... params) {
        TaskResult result = new TaskResult();
        try {
            result.output = onExecuteInBackground(params);
            result.successful = true;
        }
        catch (Exception ex) {
            result.error = ex;
            result.successful = false;
        }
        if (isCancelled()) {
            result.successful = false;
            result.error = null;
            result.output = null;
        }
        return result;
    }

    protected abstract Result onExecuteInBackground(Param... params) throws Exception;

    @NonNull
    public Exception getError() throws ExecutionException, InterruptedException {
        TaskResult result = get();
        return result.error;
    }

    @SuppressWarnings("unchecked")
    public Result getResult() throws ExecutionException, InterruptedException {
        TaskResult result = get();
        return (Result) result.output;
    }

    @Override
    protected void onPostExecute(@NonNull TaskResult result) {
        this.mProgress = null;
        update();
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Progress... values) {
        this.mProgress = values;
        update();
    }

    @Nullable
    public Progress[] getProgress() {return mProgress;}

    @Override
    protected void onCancelled() {update();}

    @MainThread
    private void update() {
        mVersion++;
        MutableLiveData<ListenableAsyncTask<?,?,?>> liveData
                = (MutableLiveData<ListenableAsyncTask<?,?,?>>) mManager.getTaskUpdateLiveData();
        liveData.postValue(this);
    }

    static class TaskResult {
        Object output = null;
        boolean successful = false;
        Exception error = null;
    }

    public static class AsyncTaskListener<Param,Progress,Result> implements Observer<ListenableAsyncTask<?,?,?>>  {

        private int mVersion = 0;

        @Override
        @SuppressWarnings("unchecked")
        public final void onChanged(ListenableAsyncTask<?, ?, ?> asyncTask) {
            Log.d(TAG,"onChange(): mVersion="+mVersion+" asyncTask.mVersion="+asyncTask.mVersion+" mTag="+ asyncTask.mTag);
            if (mVersion < asyncTask.mVersion) {
                mVersion = asyncTask.mVersion;
                AsyncTask.Status status = asyncTask.getStatus();
                if (Status.RUNNING == status) {
                    onProgressUpdate(asyncTask, (Progress[]) asyncTask.getProgress());
                } else if (Status.FINISHED == status) {
                    if (asyncTask.isCancelled()) {
                        onCanceled(asyncTask);
                    } else {
                        try {
                            TaskResult result = asyncTask.get();
                            if (result.successful) {
                                onResult(asyncTask, (Result) result.output);
                            } else {
                                onError(asyncTask, result.error);
                            }
                        } catch (ExecutionException | InterruptedException ignore) {}
                    }
                }
            }
        }

        public void onProgressUpdate(@NonNull ListenableAsyncTask<?,?,?> asyncTask, @Nullable Progress[] progress) {}

        public void onCanceled(@NonNull ListenableAsyncTask<?,?,?> asyncTask) {}

        public void onResult(@NonNull ListenableAsyncTask<?,?,?> asyncTask, @Nullable Result result) {}

        public void onError(@NonNull ListenableAsyncTask<?,?,?> asyncTask, @NonNull Exception error) {}
    }
}
