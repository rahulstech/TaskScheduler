package rahulstech.android.taskschedulder.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import rahulstech.android.database.datatype.TimeUnit;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.taskschedulder.R;
import rahulstech.android.taskschedulder.dialog.DialogUtil;
import rahulstech.android.taskschedulder.viewmodel.TaskReminderFragmentViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.text.SpannableTextBuilder;

import static rahulstech.android.taskschedulder.util.Constant.EXTRA_TASK_ID;

@SuppressWarnings({"unused","FieldMayBeFinal"})
public class TaskReminderFragment extends Fragment {

    private static final String TAG = TaskReminderFragment.class.getSimpleName();

    private static final String KEY_HAS_TASK_FETCHED = "key_task_fetched";

    private static final String KEY_PICKED_TIME = "key_picked_time";

    private TaskReminderFragmentViewModel mViewModel;

    private NavController mNavController;

    private boolean mHasTaskFetched = false;
    private TaskModel mTask;

    private TextView mTaskTime;
    private SwitchCompat mToggleTimeReminderInput;
    private ViewGroup mContainerTimeReminderInput;
    private Spinner mRemindBeforeChooser;
    private EditText mBefore;
    private Spinner mUnit;
    private RadioGroup mReminderOption;
    private ViewGroup mContainerRemindBefore;
    private ViewGroup mContainerCustomRemindBefore;

    private LocalTime mPickedTime;

    public TaskReminderFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(TaskReminderFragmentViewModel.class);
        mViewModel.getTaskById(getExtraTaskId()).observe(this,this::onTaskFetched);
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(this,mSetListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(view);
        mTaskTime = view.findViewById(R.id.task_time);
        mToggleTimeReminderInput = view.findViewById(R.id.toggle_time_reminder_input);
        mContainerTimeReminderInput = view.findViewById(R.id.container_time_reminder_input);
        mReminderOption = view.findViewById(R.id.reminder_options);
        mContainerRemindBefore = view.findViewById(R.id.container_remind_before_input);
        mRemindBeforeChooser = view.findViewById(R.id.choose_remind_before);
        mContainerCustomRemindBefore = view.findViewById(R.id.container_custom_remind_before_input);
        mBefore = view.findViewById(R.id.input_before);
        mUnit = view.findViewById(R.id.choose_unit);
        mReminderOption.setOnCheckedChangeListener((cb,which)->onTimeReminderOptionChanged(R.id.btn_remind_on_time == which));
        mRemindBeforeChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onChangeRemindBefore(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mTaskTime.setOnClickListener(v->onPickTaskTime());
        mToggleTimeReminderInput.setOnCheckedChangeListener((cb,checked)->onToggleSetTimeReminder(checked));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle state) {
        super.onViewStateRestored(state);
        if (null != state) {
            mHasTaskFetched = state.getBoolean(KEY_HAS_TASK_FETCHED,false);
            String timeText = state.getString(KEY_PICKED_TIME,null);
            if (null != timeText) {
                LocalTime time = LocalTime.parse(timeText,DateTimeFormatter.ISO_LOCAL_TIME);
                setTaskTime(time);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setTitle(R.string.label_task_reminder);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_TASK_FETCHED,mHasTaskFetched);
        if (null != mPickedTime) {
            outState.putString(KEY_PICKED_TIME,mPickedTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.task_reminder_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save) {
            onClickSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public long getExtraTaskId() {
        return requireArguments().getLong(EXTRA_TASK_ID,0);
    }

    private void onTaskFetched(@Nullable TaskModel task) {
        if (null != task) {
            setTask(task);
            mHasTaskFetched = true;
            setHasOptionsMenu(true);
        }
        else {
            showTaskNotFoundAndExit();
        }
    }

    private void setTask(@NonNull TaskModel task) {

        // TODO: hide loading view
        mTask = task;
        if (!mHasTaskFetched) {
            setTaskTime(task.getTime());
            setTimeReminder(task.getReminder());
        }
    }

    private void setTaskTime(LocalTime time) {
        mPickedTime = time;
        if (null == time) {
            mTaskTime.setText(SpannableTextBuilder.text(getText(R.string.message_task_time_not_set))
                            .relativeSize(1.5f).bold().color(Color.BLACK)
                    .build());
            mToggleTimeReminderInput.setChecked(false);
            mToggleTimeReminderInput.setEnabled(false);
        }
        else {
            String hint = getString(R.string.label_task_time_start);
            String text_time = time.format(DateTimeFormatter.ofPattern("hh : mm a", Locale.ENGLISH));
            int len_hint = hint.length();
            int len_text_time = text_time.length();
            int start_text_time = len_hint+1;
            int end_text_time = start_text_time+len_text_time;
            CharSequence text = SpannableTextBuilder.text(hint+"\n"+text_time)
                    .relativeSize(1.5f,start_text_time,end_text_time)
                    .bold(start_text_time,end_text_time)
                    .color(Color.BLACK,start_text_time,end_text_time)
                    .build();
            mTaskTime.setText(text);
            mToggleTimeReminderInput.setEnabled(true);
            mToggleTimeReminderInput.setChecked(false);
        }
    }

    private void setTimeReminder(@Nullable LocalDateTime reminder) {
        if (null == reminder) {
            mToggleTimeReminderInput.setChecked(false);
        }
        else if (null != mPickedTime){
            LocalDateTime taskDateTime = LocalDateTime.of(mTask.getDate(),mPickedTime);
            Duration diff = Duration.between(reminder,taskDateTime);
            long days = diff.toDays();
            long hours = diff.toHours();
            long minutes = diff.toMinutes();
            if (minutes == 0) {
                mReminderOption.check(R.id.btn_remind_on_time);
            }
            else {
                mReminderOption.check(R.id.btn_remind_before);
                if (minutes == 5) {
                    mRemindBeforeChooser.setSelection(0);
                }
                else if (minutes == 10) {
                    mRemindBeforeChooser.setSelection(1);
                }
                else if (minutes == 15) {
                    mRemindBeforeChooser.setSelection(2);
                }
                else if (minutes == 30) {
                    mRemindBeforeChooser.setSelection(3);
                }
                else if (minutes < 60) {
                    mRemindBeforeChooser.setSelection(9);
                    mBefore.setText(String.valueOf(minutes));
                    mUnit.setSelection(0);
                }
                else if (hours == 1) {
                    mRemindBeforeChooser.setSelection(4);
                }
                else if (hours == 2) {
                    mRemindBeforeChooser.setSelection(5);
                }
                else if (hours == 6) {
                    mRemindBeforeChooser.setSelection(6);
                }
                else if (hours == 12) {
                    mRemindBeforeChooser.setSelection(7);
                }
                else if (hours < 24) {
                    mRemindBeforeChooser.setSelection(9);
                    mBefore.setText(String.valueOf(hours));
                    mUnit.setSelection(1);
                }
                else if (days == 1) {
                    mRemindBeforeChooser.setSelection(8);
                }
                else {
                    mRemindBeforeChooser.setSelection(9);
                    mBefore.setText(String.valueOf(days));
                    mUnit.setSelection(2);
                }
            }
            mToggleTimeReminderInput.setChecked(true);
        }
    }

    /**
     *
     * @return the date and time for reminder as an instance of {@link LocalDateTime}
     * @throws IllegalArgumentException exception thrown if remind before edit text is empty
     *          or contains illegal value
     */
    @Nullable
    private LocalDateTime getPickedTimeReminder() {
        if (!mToggleTimeReminderInput.isChecked()) return null;
        boolean remindOnTime = mReminderOption.getCheckedRadioButtonId() == R.id.btn_remind_on_time;
        LocalDate date = mTask.getDate();
        LocalTime time = mPickedTime;
        LocalDateTime reminder = LocalDateTime.of(date,time);
        if (remindOnTime) return reminder;
        int before;
        TimeUnit unit;
        int remindBeforeSelection = mRemindBeforeChooser.getSelectedItemPosition();
        if (remindBeforeSelection == 0) {
            before = 5;
            unit = TimeUnit.MINUTE;
        }
        else if (remindBeforeSelection == 1) {
            before = 10;
            unit = TimeUnit.MINUTE;
        }
        else if (remindBeforeSelection == 2) {
            before = 15;
            unit = TimeUnit.MINUTE;
        }
        else if (remindBeforeSelection == 3) {
            before = 30;
            unit = TimeUnit.MINUTE;
        }
        else if (remindBeforeSelection == 4) {
            before = 1;
            unit = TimeUnit.HOUR;
        }
        else if (remindBeforeSelection == 5) {
            before = 2;
            unit = TimeUnit.HOUR;
        }
        else if (remindBeforeSelection == 6) {
            before = 6;
            unit = TimeUnit.HOUR;
        }
        else if (remindBeforeSelection == 7) {
            before = 12;
            unit = TimeUnit.HOUR;
        }
        else if (remindBeforeSelection == 8) {
            before = 1;
            unit = TimeUnit.DAY;
        }
        else {
            String beforeText = mBefore.getText().toString();
            int unitSelection = mUnit.getSelectedItemPosition();
            if (TextUtils.isEmpty(beforeText)) {
                Toast.makeText(requireContext(),R.string.message_error_remind_before_empty,Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException();
            }
            before = Integer.parseInt(beforeText);
            if (before <= 0) {
                Toast.makeText(requireContext(),R.string.message_error_remind_before_invalid_number,Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException();
            }
            if (unitSelection == 0) unit = TimeUnit.MINUTE;
            else if (unitSelection == 1) unit = TimeUnit.HOUR;
            else unit = TimeUnit.DAY;
        }
        switch (unit) {
            case DAY: reminder = reminder.minusDays(before);
            break;
            case HOUR: reminder = reminder.minusHours(before);
            break;
            default: reminder =reminder.minusMinutes(before);
        }
        return reminder;
    }

    private void onPickTaskTime() {
        if (null == mPickedTime) {
            onShowTaskTimePicker(LocalTime.MIDNIGHT);
        }
        else {
            DialogUtil.showSingleChoiceDialog(requireContext(),null,
                    getResources().getTextArray(R.array.change_task_time_start_options),(di,which)->{
                di.dismiss();
                if (0==which) {
                    onShowTaskTimePicker(mPickedTime);
                }
                else {
                    setTaskTime(null);
                }
            }).show();
        }
    }

    private void onShowTaskTimePicker(@NonNull LocalTime time) {
        TimePickerDialog picker = new TimePickerDialog(requireContext(),(di,hour,minute)->{
            LocalTime newTime = LocalTime.of(hour,minute);
            setTaskTime(newTime);
        },
                time.getHour(),time.getMinute(),false);
        picker.show();
    }

    private void onToggleSetTimeReminder(boolean setReminder) {
        mContainerTimeReminderInput.setVisibility(setReminder ? View.VISIBLE : View.GONE);
    }

    private void onTimeReminderOptionChanged(boolean remindOnTime) {
        mContainerRemindBefore.setVisibility(remindOnTime ? View.GONE : View.VISIBLE);
    }

    private void onChangeRemindBefore(int which) {
        toggleCustomRemindBefore(which == 9);
    }

    private void toggleCustomRemindBefore(boolean show) {
        mContainerCustomRemindBefore.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void onClickSave() {
        LocalDateTime reminder;
        try {
            reminder = getPickedTimeReminder();
        }
        catch (Exception ignore) {
            return;
        }
        Task newValues = new Task();
        newValues.setId(mTask.getId());
        newValues.setOrigin(mTask.getOrigin());
        newValues.setTime(mPickedTime);
        newValues.setReminder(reminder);
        int connections = mTask.getConnections();
        if (connections > 1) {
            onChooseTask(newValues);
        }
        else {
            onSaveCurrentTask(newValues);
        }
    }

    private void onChooseTask(@NonNull Task newValues) {
        DialogUtil.showSingleChoiceDialog(requireContext(),null,
                getResources().getTextArray(R.array.changes_apply_to_tasks_options),(di, which)->{
            di.dismiss();
            if (0 == which) {
                onSaveConnectedTasks(false,newValues);
            }
            else if (1 == which) {
                onSaveCurrentTask(newValues);
            }
            else {
                onSaveConnectedTasks(true,newValues);
            }
        }).show();
    }

    private void onSaveCurrentTask(@NonNull Task newValues) {
        mViewModel.setForCurrentTask(newValues);
    }

    private void onSaveConnectedTasks(boolean applyToFuture, @NonNull Task newValues) {
        if (applyToFuture) {
            mViewModel.setForFutureTasks(newValues);
        }
        else {
            mViewModel.setForAllTasks(newValues);
        }
    }

    private void showTaskNotFoundAndExit() {
        Toast.makeText(requireContext(),R.string.message_error_task_not_found,Toast.LENGTH_SHORT).show();
        mNavController.popBackStack();
    }

    private ListenableAsyncTask.AsyncTaskListener mSetListener = new ListenableAsyncTask.AsyncTaskListener() {

        @Override
        public void onResult(@NonNull ListenableAsyncTask asyncTask, @Nullable Object o) {
            Toast.makeText(requireContext(),R.string.message_changes_saved,Toast.LENGTH_SHORT).show();
            // TODO: implement logic for setting alarm
            mNavController.popBackStack();
        }

        @Override
        public void onError(@NonNull ListenableAsyncTask asyncTask, @NonNull Exception error) {
            Log.e(TAG,null,error);
            Toast.makeText(requireContext(),R.string.message_changes_not_saved,Toast.LENGTH_SHORT).show();
        }
    };
}