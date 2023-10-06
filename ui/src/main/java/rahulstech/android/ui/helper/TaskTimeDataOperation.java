package rahulstech.android.ui.helper;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBDateTime;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.datatype.TaskDataType;
import rahulstech.android.database.datatype.TimeUnit;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.ui.R;
import rahulstech.android.ui.activity.CreateOrEditTaskActivity;
import rahulstech.android.ui.dialog.DialogRemindBefore;
import rahulstech.android.ui.dialog.DialogUtil;
import rahulstech.android.ui.viewmodel.TaskDataViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.text.SpannableTextBuilder;
import rahulstech.android.util.time.DateTime;

@SuppressWarnings({"unchecked"})
public class TaskTimeDataOperation extends LifecycleAwareTaskDataOperation {

    private static final String TAG = "OpTimeData";

    private TaskDataViewModel mViewModel;

    private ViewModelStoreOwner mVmStoreOwner;

    public TaskTimeDataOperation(@NonNull Context context) {
        super(context);
    }

    public void setViewModelStoreOwner(@NonNull ViewModelStoreOwner owner) {
        this.mVmStoreOwner = owner;
    }

    @NonNull
    public ViewModelStoreOwner getViewModelStoreOwner() {
        return mVmStoreOwner;
    }

    @Override
    protected void onInit() {
        mViewModel = new ViewModelProvider(getViewModelStoreOwner(),
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(TaskDataViewModel.class);
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(getLifeCycleOwner(),mSaveListener);
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(getLifeCycleOwner(),mRemoveListener);
    }

    @NonNull
    @Override
    public CharSequence getDisplayText(TaskData data, @NonNull String ifNull) {
        DBTime time = getTask().getTimeStart();
        if (null == time) return ifNull;
        String taskTime = time.format("h : mm aa");
        if (null != data) {
            DBDate date2 = data.getDate2();
            DBTime time2 = data.getTime2();
            if (null != date2 && null != time2) {
                DBDateTime reminder = DBDateTime.from(date2,time2);
                String reminderText = getString(R.string.label_next_reminder ,reminder.format("d-MMM-yy h:mm aa"));
                return new SpannableStringBuilder(taskTime+"\n")
                        .append(SpannableTextBuilder.text(reminderText).italic()
                                .relativeSize(.75f).build());
            }
        }
        return taskTime;
    }

    @Override
    public void addData() {
        TaskModel task = getTask();
        //TaskData data = new TaskData();
        //data.setTaskId(task.getId());
        //data.setDate1(task.getDateStart());
        //onPickTime(data);

        if (null == task.getTimeStart()) {
            Intent intent = new Intent(this, CreateOrEditTaskActivity.class);
            intent.setAction(CreateOrEditTaskActivity.ACTION_EDIT_TASK);
            intent.putExtra(CreateOrEditTaskActivity.EXTRA_TASK_ID,task.getId());
            startActivity(intent);
        }
    }

    @Override
    public void setData(@NonNull TaskData oldData) {
        DialogUtil.showSingleChoiceDialog(this,null,
                getResources().getTextArray(R.array.change_task_time_options),
                (di,which)->{
            di.dismiss();
            if (0 == which) {
                onPickTime(oldData);
            }
            else if (1 == which) {
                onRemoveReminder(oldData);
            }
            else {
                onRemoveTime(oldData);
            }
        }).show();
    }

    private void onPickTime(@NonNull TaskData data) {
        DBTime oldTime1 = data.getTime1();
        DateTime datetime;
        if (null != oldTime1) {
            datetime = DateTime.ofTime(oldTime1.getHourOfDay(),oldTime1.getMinute());
        }
        else {
            datetime = DateTime.now();
        }
        TimePickerDialog picker = new TimePickerDialog(this,(di, hour, minute)->{
            DBTime newTime1 = DBTime.of(hour,minute);
            data.setType(TaskDataType.TYPE_TASK_TIME_AND_REMINDER);
            data.setTime1(newTime1);
            onAddReminder(data);
        },datetime.getHourOfDay(),datetime.getMinute(),false);
        picker.show();
    }

    private void onAddReminder(@NonNull TaskData data) {
        DialogRemindBefore dialog = new DialogRemindBefore(this);
        dialog.setOnClickSaveListener((addReminder, before, unit) -> {
            if (addReminder) {
                DateTime dateTime = calculateReminderDateTime(data.getDate1(),data.getTime1(),before,unit);
                Log.d(TAG,"before="+before+" unit="+unit+" date1="+data.getDate1()+" time1="+data.getTime1()+" reminder-datetime="+dateTime);
                DBTime time2 = DBTime.of(dateTime.getHourOfDay(), dateTime.getMinute());
                DBDate date2 = DBDate.of(dateTime.getYear(),dateTime.getMonth(),dateTime.getDate());
                data.setText5(unit.name());
                data.setTime2(time2);
                data.setDate2(date2);
                data.setLong2((long) before);
            }
            else {
                data.setText5(null);
                data.setTime2(null);
                data.setDate2(null);
                data.setLong2(null);
            }
            onSaveData(data);
        });

        if (data.getId() > 0) {
            Long long2 = data.getLong2();
            String text5 = data.getText5();
            if (null != long2 && long2 > 0) {
                dialog.setIsRemindBefore(true);
                dialog.setRemindBefore(long2.intValue(), TimeUnit.valueOf(text5));
            }
            else {
                dialog.setIsRemindBefore(false);
            }
        }
        dialog.show();
    }

    private void onRemoveReminder(@NonNull TaskData data) {
        data.setText5(null);
        data.setDate2(null);
        data.setTime2(null);
        data.setLong2(null);
        onSetSingleData(data);
    }

    private void onRemoveTime(@NonNull TaskData data) {
        onRemoveSingleDate(data);
    }

    private DateTime calculateReminderDateTime(@NonNull DBDate date, @NonNull DBTime time, int before, @NonNull TimeUnit unit) {
        DateTime dateTime = DateTime.ofDateTime(date.getYear(),date.getMonth(),date.getDate(),time.getHourOfDay(),time.getMinute());
        switch (unit) {
            case MINUTE: {
                dateTime = dateTime.addMinutes(-before);
            }
            break;
            case HOUR: {
                dateTime = dateTime.addHours(-before);
            }
            break;
            case DAY: {
                dateTime = dateTime.addDays(-before);
            }
        }
        return dateTime;
    }

    private void onSaveData(@NonNull TaskData data) {
        if (data.getId() > 0) {
            onSetSingleData(data);
        }
        else {
            onAddSingleData(data);
        }
    }

    @Override
    protected void onAddMultipleData(@NonNull List<TaskData> data) {
        mViewModel.addTaskData(data);
    }

    @Override
    protected void onSetMultipleData(@NonNull List<TaskData> data) {
        mViewModel.setTaskData(data);
    }

    @Override
    protected void onRemoveMultipleData(@NonNull List<TaskData> data) {
        mViewModel.removeTaskData(data);
    }

    @Override
    protected void onSingleSaveSuccessful(@NonNull TaskData data) {
        // TODO: set or unset alarm
        Toast.makeText(this, R.string.message_changes_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveError(@Nullable Exception error) {
        Log.e(TAG,null,error);
        Toast.makeText(this, R.string.message_changes_not_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSingleRemoveSuccessful(@NonNull TaskData data) {
        // TODO: unset alarm
        Toast.makeText(this, R.string.message_changes_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRemoveError(@Nullable Exception error) {
        Log.e(TAG,null,error);
        Toast.makeText(this, R.string.message_changes_not_saved, Toast.LENGTH_SHORT).show();
    }

    private final ListenableAsyncTask.AsyncTaskListener<Void,Void,List<TaskData>> mSaveListener =
            new ListenableAsyncTask.AsyncTaskListener<Void,Void,List<TaskData>>(){

                @Override
                public void onError(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @NonNull Exception error) {
                    onSaveError(error);
                }

                @Override
                public void onResult(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @Nullable List<TaskData> data) {
                    onMultipleSaveSuccessful(data);
                }
    };

    private final ListenableAsyncTask.AsyncTaskListener<Void,Void,List<TaskData>> mRemoveListener
            = new ListenableAsyncTask.AsyncTaskListener<Void,Void,List<TaskData>>(){

        @Override
        public void onResult(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @Nullable List<TaskData> data) {
            onMultipleRemoveSuccessful(data);
        }

        @Override
        public void onError(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @NonNull Exception error) {
            onRemoveError(error);
        }
    };
}