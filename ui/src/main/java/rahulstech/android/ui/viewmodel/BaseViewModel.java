package rahulstech.android.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import rahulstech.android.util.concurrent.AsyncTasksManager;

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
}
