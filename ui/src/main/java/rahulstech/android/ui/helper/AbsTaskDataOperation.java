package rahulstech.android.ui.helper;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskModel;

@SuppressWarnings("unused")
public abstract class AbsTaskDataOperation extends ContextWrapper {

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

    public AbsTaskDataOperation(@NonNull AppCompatActivity activity) {
        this(activity,activity);
    }

    public AbsTaskDataOperation(@NonNull Context base, @NonNull LifecycleOwner owner) {
        super(base);
        owner.getLifecycle().addObserver(mObserver);
    }

    @NonNull
    public CharSequence getDisplayText(TaskData data, @NonNull String ifNull) {
        return ifNull;
    }

    protected void onPause() {}

    protected void onResume() {}

    public void addData(@NonNull TaskModel task) {
        onAddSingleData(null);
    }

    public void addMultipleData(@NonNull TaskModel task) {
        onAddMultipleData(Collections.emptyList());
    }

    public void setData(@NonNull TaskData oldData) {
        onSetSingleData(oldData);
    }

    public void setMultipleData(@NonNull List<TaskData> oldData) {
        onSetMultipleData(oldData);
    }



    public void removeData(@NonNull TaskData oldData) {
        onRemoveSingleDate(oldData);
    }

    public void removeMultipleData(@NonNull List<TaskData> oldData) {
        onRemoveMultipleData(oldData);
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

    protected void onSingleRemoveSuccessful(@NonNull TaskData data) {

    }

    protected void onRemoveError(@Nullable Exception error) {}
}
