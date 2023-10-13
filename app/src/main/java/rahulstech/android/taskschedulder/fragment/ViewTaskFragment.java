package rahulstech.android.taskschedulder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.database.model.TaskWithTaskDataModel;
import rahulstech.android.taskschedulder.R;
import rahulstech.android.taskschedulder.activity.CreateOrEditTaskActivity;
import rahulstech.android.taskschedulder.dialog.DialogUtil;
import rahulstech.android.taskschedulder.viewmodel.TaskViewModel;
import rahulstech.android.taskschedulder.viewmodel.ViewTaskViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.text.SpannableTextBuilder;

import static rahulstech.android.taskschedulder.util.Constant.ACTION_EDIT_TASK;
import static rahulstech.android.taskschedulder.util.Constant.EXTRA_TASK_ID;

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
    private TextView mTaskNote;

    private TaskWithTaskDataModel mTaskWithTaskData;
    private NavController mNavController;

    public ViewTaskFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity(),
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(ViewTaskViewModel.class);
        mTaskViewModel = new ViewModelProvider(requireActivity(),
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(TaskViewModel.class);
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(this,mDeleteTaskListener);
        mViewModel.getTaskWithDataByTaskId(getExtraTaskId()).observe(this,this::onTaskDetailsFetched);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(view);
        mLoadingView = view.findViewById(R.id.loading_view);
        mMainContent = view.findViewById(R.id.content_view);
        mDescription = view.findViewById(R.id.task_description);
        mDateStart = view.findViewById(R.id.task_date_start);
        mTaskState = view.findViewById(R.id.task_state);
        mTimeStart = view.findViewById(R.id.task_time);
        mTaskNote = view.findViewById(R.id.task_note);
        mTimeStart.setOnClickListener(v-> onClickAddTimeReminder());
        mTaskNote.setOnClickListener(v->onClickAddNote());
        view.findViewById(R.id.edit_task).setOnClickListener(v->onClickEditTask());
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setTitle(R.string.label_view_task);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.view_task_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_task) {
            onClickDeleteTask();
            return true;
        }
        else if (itemId == R.id.copy_task) {
            onClickCopyTask();
            return true;
        }
        return super.onContextItemSelected(item);
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
        // TODO: mLoadingView not hidden coming back from other fragment
        //  though coming back from other activity working
        mLoadingView.setVisibility(View.GONE);
        mMainContent.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
    }

    void setTask(@NonNull TaskModel task) {
        mDescription.setText(task.getDescription());

        LocalTime time = task.getTime();
        LocalDateTime reminder = task.getReminder();
        if (null == time) {
            mTimeStart.setText(SpannableTextBuilder.text(getString(R.string.message_task_time_not_set)).bold().build());
        }
        else if (null == reminder) {
            mTimeStart.setText(SpannableTextBuilder.text(time.format(DateTimeFormatter.ofPattern("hh : mm a"))).bold().build());
        }
        else {
            String timeText = time.format(DateTimeFormatter.ofPattern("hh : mm a"));
            String reminderText = reminder.format(DateTimeFormatter.ofPattern("d-MMM-yy h:mm a"));
            CharSequence spannedTimeText = SpannableTextBuilder.text(timeText).bold().build();
            CharSequence iconText = SpannableTextBuilder.text(" ").image(requireContext(),R.drawable.ic_baseline_reminder_24).build();
            CharSequence spannedReminderText = SpannableTextBuilder.text(reminderText).relativeSize(.75f).italic().build();
            CharSequence text = new SpannableStringBuilder()
                    .append(spannedTimeText).append("\n")
                    .append(iconText).append(" ")
                    .append(spannedReminderText);
            mTimeStart.setText(text);
        }

        LocalDate date = task.getDate();
        mDateStart.setText(date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
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

        String note = task.getNote();
        if (TextUtils.isEmpty(note)) {
            mTaskNote.setText(SpannableTextBuilder.text(getText(R.string.message_no_note)).bold().relativeSize(1.5f).build());
        }
        else {
            mTaskNote.setText(note);
        }
    }

    void onClickAddTimeReminder() {
        Bundle args = new Bundle();
        args.putLong(EXTRA_TASK_ID,getExtraTaskId());
        mNavController.navigate(R.id.action_viewTaskFragment_to_taskReminderFragment,args);
    }

    void onClickAddNote() {
        Bundle args = new Bundle();
        args.putLong(EXTRA_TASK_ID,getExtraTaskId());
        mNavController.navigate(R.id.action_viewTaskFragment_to_taskNoteFragment,args);
    }

    void onClickEditTask() {
        TaskModel task = mTaskWithTaskData.getTask();
        Intent i = new Intent(requireContext(), CreateOrEditTaskActivity.class);
        i.setAction(ACTION_EDIT_TASK);
        i.putExtra(EXTRA_TASK_ID,task.getId());
        startActivity(i);
    }

    void onClickDeleteTask() {
        // TODO: recheck delete task
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

    void onClickCopyTask() {
        Bundle args = new Bundle();
        args.putLong(EXTRA_TASK_ID,getExtraTaskId());
        mNavController.navigate(R.id.action_viewTaskFragment_to_copyTaskFragment,args);
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