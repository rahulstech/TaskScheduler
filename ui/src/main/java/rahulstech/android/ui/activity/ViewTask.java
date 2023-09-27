package rahulstech.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.TaskDataType;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.database.model.TaskWithTaskDataModel;
import rahulstech.android.ui.R;
import rahulstech.android.ui.dialog.DialogUtil;
import rahulstech.android.ui.helper.TaskTimeDataOperation;
import rahulstech.android.ui.viewmodel.ViewTaskViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.time.DateTime;

@SuppressWarnings("unused")
public class ViewTask extends AppCompatActivity {

    private static final String TAG = "ViewTask";

    private static final int CODE_DELETE_TASK = 0;

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private ViewTaskViewModel mViewModel;

    private TextView mDescription;
    private TextView mDateStart;
    private TextView mTaskTime;
    private TextView mTaskState;

    private TaskWithTaskDataModel mTaskWithTaskData;

    private TaskTimeDataOperation mTimeDataOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        mViewModel = new ViewModelProvider((ViewModelStoreOwner) this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(ViewTaskViewModel.class);

        mDescription = findViewById(R.id.task_description);
        mDateStart = findViewById(R.id.task_date_start);
        mTaskState = findViewById(R.id.task_state);
        mTaskTime = findViewById(R.id.task_time);
        mTaskTime.setOnClickListener(v-> onClickAddTimeReminder());
        findViewById(R.id.edit_task).setOnClickListener(v->onClickEditTask());

        long taskId = getExtraTaskId();
        if (taskId < 0) {
            showTaskNotFoundAndExit();
            return;
        }

        mViewModel.getTaskWithDataByTaskId(taskId).observe(this,this::onTaskDetailsFetched);

        mTimeDataOperation = new TaskTimeDataOperation(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addDeleteTaskAsyncTaskLister();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeDeleteTaskAsyncTaskListener();
    }

    void addDeleteTaskAsyncTaskLister() {
        ListenableAsyncTask<Void,Void,Boolean> asyncTask = mViewModel.getAsyncTaskManager().getTask(CODE_DELETE_TASK);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(mDeleteTaskListener);
        }
    }

    void removeDeleteTaskAsyncTaskListener() {
        ListenableAsyncTask<Void,Void,Boolean> asyncTask = mViewModel.getAsyncTaskManager().getTask(CODE_DELETE_TASK);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_task_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_task) {
            onClickDeleteTask();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    long getExtraTaskId() {
        return getIntent().getLongExtra(EXTRA_TASK_ID,0);
    }

    void onTaskDetailsFetched(TaskWithTaskDataModel details) {
        mTaskWithTaskData = details;
        if (null == details) {
            showTaskNotFoundAndExit();
        }
        else {
            setTask(details.getTask());
            setTaskData(details);
        }
    }

    boolean hasTaskDetails() {
        return null != mTaskWithTaskData;
    }

    void setTask(@NonNull TaskModel task) {
        mDescription.setText(task.getDescription());
        DBDate dateStart = task.getDateStart();
        DateTime date = DateTime.ofDate(dateStart.getYear(),dateStart.getMonth(),dateStart.getDate());
        mDateStart.setText(date.format("dd MMMM yyyy"));
        mTaskState.setText(task.getState().toString());
    }

    void setTaskData(@NonNull TaskWithTaskDataModel details) {
        TaskData taskTime = details.getSingleDataForType(TaskDataType.TYPE_TASK_TIME_AND_REMINDER);
        setTaskTime(taskTime);
    }

    void setTaskTime(TaskData data) {
        mTaskTime.setText(mTimeDataOperation.getDisplayText(data,
                getString(R.string.message_task_time_not_set)));
    }

    void onClickAddTimeReminder() {
        if (!hasTaskDetails()) return;
        TaskModel task = mTaskWithTaskData.getTask();
        TaskData taskTime = mTaskWithTaskData.getSingleDataForType(TaskDataType.TYPE_TASK_TIME_AND_REMINDER);
        if (null == taskTime) {
            mTimeDataOperation.addData(task);
        }
        else {
            mTimeDataOperation.setData(taskTime);
        }
    }

    void onClickEditTask() {
        if (null == mTaskWithTaskData) return;
        TaskModel task = mTaskWithTaskData.getTask();
        Intent i = new Intent(this,CreateOrEditTaskActivity.class);
        i.setAction(CreateOrEditTaskActivity.ACTION_EDIT_TASK);
        i.putExtra(CreateOrEditTaskActivity.EXTRA_TASK_ID,task.getId());
        startActivity(i);
    }

    void onClickDeleteTask() {
        if (null == mTaskWithTaskData) return;
        final TaskModel task = mTaskWithTaskData.getTask();
        DialogUtil.confirmDialog(this,getString(R.string.message_warning_delete_task,task.getDescription()),
                getString(R.string.label_cancel),null,
                getString(R.string.label_delete_task),(di,which)->onDeleteTask(task)).show();
    }

    void onDeleteTask(@NonNull TaskModel task) {
        ListenableAsyncTask<Void,Void,Boolean> asyncTask = mViewModel.removeTask(task.getId());
        asyncTask.setAsyncTaskLister(mDeleteTaskListener);
        asyncTask.start(CODE_DELETE_TASK);
    }

    void onTaskDeleted(boolean successful, Exception error) {
        if (!successful) {
            Log.e(TAG,null,error);
            Toast.makeText(this,R.string.message_error_task_not_removed,Toast.LENGTH_SHORT).show();
        }
    }

    void showTaskNotFoundAndExit() {
        Toast.makeText(this, R.string.message_error_task_not_found, Toast.LENGTH_SHORT).show();
        finish();
    }

    ListenableAsyncTask.AsyncTaskListener<Void,Void,Boolean> mDeleteTaskListener
            = new ListenableAsyncTask.SimpleAsyncTaskListener<Void,Void,Boolean>(){

        @Override
        public void onResult(Boolean deleted) {
            mViewModel.getAsyncTaskManager().removeTask(CODE_DELETE_TASK);
            onTaskDeleted(deleted,null);
        }

        @Override
        public void onError(@NonNull Exception error) {
            mViewModel.getAsyncTaskManager().removeTask(CODE_DELETE_TASK);
            onTaskDeleted(false,error);
        }
    };
}