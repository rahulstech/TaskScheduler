package rahulstech.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.database.model.TaskWithTaskDataModel;
import rahulstech.android.ui.R;
import rahulstech.android.ui.activity.CreateOrEditTaskActivity;
import rahulstech.android.ui.dialog.DialogUtil;
import rahulstech.android.ui.helper.TaskTimeDataOperation;
import rahulstech.android.ui.viewmodel.TaskViewModel;
import rahulstech.android.ui.viewmodel.ViewTaskViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.text.SpannableTextBuilder;

import static rahulstech.android.ui.activity.ViewTask.EXTRA_TASK_ID;

public class ViewTaskFragment extends Fragment {

    private static final String TAG = "ViewTaskFragment";

    private ViewTaskViewModel mViewModel;
    private TaskViewModel mTaskViewModel;

    private View mLoadingView;
    private View mMainContent;
    private TextView mDescription;
    private TextView mDateStart;
    private TextView mTimeStart;
    private TextView mTaskState;

    private TaskWithTaskDataModel mTaskWithTaskData;

    private TaskTimeDataOperation mTimeDataOperation;

    public ViewTaskFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = new ViewModelProvider(requireActivity(),
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(ViewTaskViewModel.class);
        mTaskViewModel = new ViewModelProvider(requireActivity(),
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(TaskViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mLoadingView = view.findViewById(R.id.loading_view);
        mMainContent = view.findViewById(R.id.content_view);
        mDescription = view.findViewById(R.id.task_description);
        mDateStart = view.findViewById(R.id.task_date_start);
        mTaskState = view.findViewById(R.id.task_state);
        mTimeStart = view.findViewById(R.id.task_time);
        mTimeStart.setOnClickListener(v-> onClickAddTimeReminder());
        view.findViewById(R.id.edit_task).setOnClickListener(v->onClickEditTask());
        mTimeDataOperation = new TaskTimeDataOperation(requireContext());
        mTimeDataOperation.setViewModelStoreOwner(requireActivity());
        mTimeDataOperation.setLifecycleOwner(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        long taskId = getExtraTaskId();
        if (taskId <= 0) {
            showTaskNotFoundAndExit();
            return;
        }
        setHasOptionsMenu(false);
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(this,mDeleteTaskListener);
        mViewModel.getTaskWithDataByTaskId(taskId).observe(this,this::onTaskDetailsFetched);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.view_task_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        boolean handled = true;
        if (itemId == R.id.delete_task) {
            onClickDeleteTask();
        }
        else if (itemId == R.id.history) {
            onClickTaskHistory();
        }
        else if (itemId == R.id.copy_task) {
            onClickCopyTask();
        }
        else {
            handled = false;
        }
        return handled;
    }

    long getExtraTaskId() {
        return requireArguments().getLong(EXTRA_TASK_ID,0);
    }

    void onTaskDetailsFetched(TaskWithTaskDataModel details) {
        mTaskWithTaskData = details;
        if (null == details) {
            showTaskNotFoundAndExit();
        }
        else {
            showTaskDetails(details);
        }
    }

    void showTaskDetails(@NonNull TaskWithTaskDataModel details) {
        TaskModel task = details.getTask();
        setTask(task);

        mTimeDataOperation.setTask(task);

        mLoadingView.setVisibility(View.GONE);
        mMainContent.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
    }

    void setTask(@NonNull TaskModel task) {
        mDescription.setText(task.getDescription());
        DBDate dateStart = task.getDateStart();
        mDateStart.setText(dateStart.format("dd MMMM yyyy"));
        switch (task.getState()) {
            case COMPLETE: {
                mTaskState.setText(getString(R.string.label_state_complete));
            }
            break;
            case CANCEL: {
                mTaskState.setText(getString(R.string.label_state_cancel));
            }
            break;
            default: {
                mTaskState.setText(getString(R.string.label_state_pending));
            }
        }
    }

    void onClickAddTimeReminder() {
        // TODO: add reminder
    }

    void onClickEditTask() {
        TaskModel task = mTaskWithTaskData.getTask();
        Intent i = new Intent(requireContext(), CreateOrEditTaskActivity.class);
        i.setAction(CreateOrEditTaskActivity.ACTION_EDIT_TASK);
        i.putExtra(CreateOrEditTaskActivity.EXTRA_TASK_ID,task.getId());
        startActivity(i);
    }

    void onClickDeleteTask() {
        final TaskModel task = mTaskWithTaskData.getTask();
        String description = task.getDescription();
        String message = getString(R.string.message_warning_delete_task,description);
        int start = message.indexOf(description), end = start+description.length();
        DialogUtil.confirmDialog(requireContext(),SpannableTextBuilder.text(message).bold(start,end)
                        .relativeSize(1.15f,start,end).build(),
                getText(R.string.label_cancel),null,
                getText(R.string.label_delete),(di, which)->onDeleteTask(task))
                .show();
    }

    void  onClickTaskHistory() {
        // TODO: show task history
    }

    void onClickCopyTask() {
        // TODO: copy task
    }

    void onDeleteTask(@NonNull TaskModel task) {
        mTaskViewModel.removeTask(task);
    }

    void onTaskDeleted(boolean successful, Exception error) {
        if (!successful) {
            Log.e(TAG,null,error);
            Toast.makeText(requireContext(),R.string.message_error_task_not_removed,Toast.LENGTH_SHORT).show();
        }
    }

    void showTaskNotFoundAndExit() {
        Toast.makeText(requireContext(), R.string.message_error_task_not_found, Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }

    private ListenableAsyncTask.AsyncTaskListener<Void,Void,Boolean> mDeleteTaskListener
            = new ListenableAsyncTask.AsyncTaskListener<Void,Void,Boolean>(){

        @Override
        public void onResult(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @Nullable Boolean result) {
            onTaskDeleted(result,null);
        }

        @Override
        public void onError(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @NonNull Exception error) {
            onTaskDeleted(false,error);
        }
    };
}