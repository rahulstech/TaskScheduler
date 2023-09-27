package rahulstech.android.util.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class AppExecutors {

    private static class MainExecutor implements Executor {
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mHandler.post(command);
        }
    }

    private static Executor mBgExecutor = null;
    private static MainExecutor mMainExecutor = null;

    @NonNull
    public static Executor getBackgroundExecutor() {
        if (null == mBgExecutor) {
            mBgExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        return mBgExecutor;
    }

    @NonNull
    public static Executor getMainExecutor() {
        if (null == mMainExecutor) {
            mMainExecutor = new MainExecutor();
        }
        return mMainExecutor;
    }
}
