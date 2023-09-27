package rahulstech.android.util.concurrent;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;

import static rahulstech.android.util.concurrent.AppExecutors.getBackgroundExecutor;

@SuppressWarnings("unused")
public abstract class ListenableAsyncTask<Param,Progress,Result> extends AsyncTask<Param,Progress, ListenableAsyncTask.TaskResult> {

    private static final String TAG = "ListenableAsyncTask";

    private final AsyncTasksManager mManager;
    private final AsyncTaskListenerWrapper<Param,Progress,Result> mListener = new AsyncTaskListenerWrapper<>();

    public ListenableAsyncTask(@NonNull AsyncTasksManager manager) {
        mManager = manager;
    }

    public void setAsyncTaskLister(AsyncTaskListener<Param,Progress,Result> listener) {
        mListener.wrap(listener);
        notifyNowIfNeeded();
    }

    private void notifyNowIfNeeded() {
        Status s = getStatus();
        if (Status.FINISHED == s) {
            if (isCancelled()) {
                mListener.onCanceled();
            }
            else {
                try {
                    TaskResult result = get();
                    if (null != result.error) {
                        mListener.onError(getError());
                    } else {
                        mListener.onResult(getResult());
                    }
                } catch (InterruptedException | ExecutionException ignore) {
                }
            }
        }
    }

    public void start(int id, Param... params) {
        mManager.addAsyncTask(id,this);
        executeOnExecutor(getBackgroundExecutor(),params);
    }

    @SafeVarargs
    @Override
    protected final TaskResult doInBackground(Param... params) {
        TaskResult result = new TaskResult();
        try {
            result.output = onExecuteInBackground(params);
        }
        catch (Exception ex) {
            result.error = ex;
        }
        return result;
    }

    protected abstract Result onExecuteInBackground(Param... params) throws Exception;


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
    @SuppressWarnings("unchecked")
    protected void onPostExecute(TaskResult result) {
        if (null == result.error) {
            mListener.onResult((Result) result.output);
        }
        else {
            mListener.onError(result.error);
        }
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        mListener.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        mListener.onCanceled();
    }

    public static class TaskResult {
        Object output = null;
        Exception error = null;
    }

    public interface AsyncTaskListener<Param,Progress,Result> {

        void onProgressUpdate(Progress... progress);

        void onCanceled();

        void onResult(Result result);

        void onError(@NonNull Exception error);
    }

    public static class SimpleAsyncTaskListener<Param,Progress,Result> implements AsyncTaskListener<Param,Progress,Result> {

        @Override
        public void onProgressUpdate(Progress... progress) {}

        @Override
        public void onCanceled() {}

        @Override
        public void onResult(Result result) {}

        @Override
        public void onError(@NonNull Exception error) {}
    }

    private static class AsyncTaskListenerWrapper<Param,Progress,Result> implements AsyncTaskListener<Param,Progress,Result> {

        private AsyncTaskListener<Param,Progress,Result> mWrapped = null;


        public boolean hasListener() {
            return null != mWrapped;
        }

        public void wrap(AsyncTaskListener<Param,Progress,Result> listener) {
            mWrapped = listener;
        }

        @Override
        public void onProgressUpdate(Progress... progress) {
            if (hasListener()) {
                mWrapped.onProgressUpdate(progress);
            }
        }

        @Override
        public void onCanceled() {
            if (hasListener()) {
                mWrapped.onCanceled();
            }
        }

        @Override
        public void onResult(Result result) {
            Log.d(TAG,"hasListener="+hasListener()+" result="+result);
            if (hasListener()) {
                mWrapped.onResult(result);
            }
        }

        @Override
        public void onError(@NonNull Exception error) {
            if (hasListener()) {
                mWrapped.onError(error);
            }
        }
    }
}
