package rahulstech.android.ui.helper;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskModel;

@SuppressWarnings("unused")
public abstract class LifecycleAwareTaskDataOperation extends ContextWrapper {

    private final LifecycleEventObserver mObserver = (source, event) -> {
        switch (event) {
            case ON_PAUSE: {
                onPause();
            }
            break;
            case ON_RESUME: {
                onResume();
            }
        }
    };

    private LifecycleOwner mLifecycleOwner;

    private TaskModel mTask;

    public LifecycleAwareTaskDataOperation(@NonNull Context context) {
        super(context);
    }

    public void setLifecycleOwner(@NonNull LifecycleOwner owner) {
        mLifecycleOwner = owner;
        owner.getLifecycle().addObserver(mObserver);
        onInit();
    }

    @NonNull
    public LifecycleOwner getLifeCycleOwner() {
        return mLifecycleOwner;
    }

    public void setTask(@NonNull TaskModel task) {
        this.mTask = task;
    }

    public TaskModel getTask() {
        return mTask;
    }

    @NonNull
    public CharSequence getDisplayText(TaskData data, @NonNull String ifNull) {
        return ifNull;
    }

    protected void onInit() {}

    protected void onPause() {}

    protected void onResume() {}

    public void addData() {
        throw new RuntimeException("addData() not implemented");
    }

    public void addMultipleData() {
        throw new RuntimeException("addData() not implemented");
    }

    public void setData(@NonNull TaskData oldData) {
        throw new RuntimeException("addData() not implemented");
    }

    public void setMultipleData(@NonNull List<TaskData> oldData) {
        throw new RuntimeException("addData() not implemented");
    }

    public void removeData(@NonNull TaskData oldData) {
        throw new RuntimeException("addData() not implemented");
    }

    public void removeMultipleData(@NonNull List<TaskData> oldData) {
        throw new RuntimeException("addData() not implemented");
    }

    protected void onAddSingleData(@NonNull TaskData data) {
        onAddMultipleData(Collections.singletonList(data));
    }

    protected abstract void onAddMultipleData(@NonNull List<TaskData> data);

    protected void onSetSingleData(@NonNull TaskData data) {
        onSetMultipleData(Collections.singletonList(data));
    }

    protected abstract void onSetMultipleData(@NonNull List<TaskData> data);

    protected void onRemoveSingleDate(@NonNull TaskData data) {
        onRemoveMultipleData(Collections.singletonList(data));
    }

    protected abstract void onRemoveMultipleData(@NonNull List<TaskData> data);

    protected void onMultipleSaveSuccessful(@NonNull List<TaskData> data){
        if (data.size() == 1) {
            onSingleSaveSuccessful(data.get(0));
        }
    }

    protected void onSingleSaveSuccessful(@NonNull TaskData data){}

    protected void onSaveError(@Nullable Exception error) {}

    protected void onMultipleRemoveSuccessful(@NonNull List<TaskData> data) {
        if (data.size() == 1) {
            onSingleRemoveSuccessful(data.get(0));
        }
    }

    protected void onSingleRemoveSuccessful(@NonNull TaskData data) {}

    protected void onRemoveError(@Nullable Exception error) {}
}
