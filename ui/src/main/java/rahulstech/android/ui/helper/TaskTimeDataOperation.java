package rahulstech.android.ui.helper;

import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.datatype.DurationUnit;
import rahulstech.android.database.datatype.TaskDataType;
import rahulstech.android.database.entity.TaskData;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.ui.R;
import rahulstech.android.ui.dialog.DialogRemindBefore;
import rahulstech.android.ui.util.TextUtil;
import rahulstech.android.ui.viewmodel.TaskDataViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.time.DateTime;

public class TaskTimeDataOperation extends AbsTaskDataOperation {

    private static final String TAG = "OpTimeData";

    private static final int CODE_SAVE = 1;

    private static final int CODE_REMOVE = 2;

    private TaskDataViewModel mViewModel;

    public TaskTimeDataOperation(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mViewModel) {
            mViewModel = new ViewModelProvider((ViewModelStoreOwner) getBaseContext(),
                    (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                    .get(TaskDataViewModel.class);
        }
        addAsyncTaskSaveListener();
        addAsyncTaskRemoveListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeAsyncTaskSaveListener();
        removeAsyncTaskRemoveListener();
    }

    @NonNull
    @Override
    public CharSequence getDisplayText(TaskData data, @NonNull String ifNull) {
        String text;
        if (null != data) {
            DBTime time1 = data.getTime1();
            DBDate date2 = data.getDate2();
            DBTime time2 = data.getTime2();
            DateTime time = DateTime.ofTime(time1.getHourOfDay(),time1.getMinute());
            String taskTime = time.format("h : mm aa");
            if (null != date2 && null != time2) {
                DateTime reminder = DateTime.ofDateTime(date2.getYear(), date2.getMonth(), date2.getDate(), time2.getHourOfDay(), time2.getMinute());
                String plainText = taskTime+"\n(Remind at "+reminder.format("d MMM yy h:mm aa")+") ";
                int start = taskTime.length()+1, end = plainText.length()-1;
                return TextUtil.italic(TextUtil.relativeSize(plainText,.75f,start,end), start,end);
            }
            else {
                return taskTime;
            }
        }
        return ifNull;
    }

    @Override
    public void addData(@NonNull TaskModel task) {
        TaskData data = new TaskData();
        data.setTaskId(task.getId());
        data.setDate1(task.getDateStart());
        onPickTime(data);
    }

    @Override
    public void setData(@NonNull TaskData oldData) {
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(R.array.change_task_time_options,0,(di, which)->{
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
                })
                .show();
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
                dialog.setRemindBefore(long2.intValue(), DurationUnit.valueOf(text5));
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

    private DateTime calculateReminderDateTime(@NonNull DBDate date, @NonNull DBTime time, int before, @NonNull DurationUnit unit) {
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
        ListenableAsyncTask<Void,Void,List<TaskData>> asyncTask = mViewModel.addTaskData(data);
        asyncTask.setAsyncTaskLister(mSaveListener);
        asyncTask.start(CODE_SAVE);
    }

    @Override
    protected void onSetMultipleData(@NonNull List<TaskData> data) {
        ListenableAsyncTask<Void,Void,List<TaskData>> asyncTask = mViewModel.setTaskData(data);
        asyncTask.setAsyncTaskLister(mSaveListener);
        asyncTask.start(CODE_SAVE);
    }

    @Override
    protected void onRemoveMultipleData(@NonNull List<TaskData> data) {
        ListenableAsyncTask<Void,Void,List<TaskData>> asyncTask = mViewModel.removeTaskData(data);
        asyncTask.setAsyncTaskLister(mRemoveListener);
        asyncTask.start(CODE_REMOVE);
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

    private void addAsyncTaskSaveListener() {
        ListenableAsyncTask asyncTask = mViewModel.getAsyncTaskManaget().getTask(CODE_SAVE);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(mSaveListener);
        }
    }

    private void removeAsyncTaskSaveListener() {
        ListenableAsyncTask asyncTask = mViewModel.getAsyncTaskManaget().getTask(CODE_SAVE);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(null);
        }
    }

    private void addAsyncTaskRemoveListener() {
        ListenableAsyncTask asyncTask = mViewModel.getAsyncTaskManaget().getTask(CODE_REMOVE);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(mRemoveListener);
        }
    }

    private void removeAsyncTaskRemoveListener() {
        ListenableAsyncTask asyncTask = mViewModel.getAsyncTaskManaget().getTask(CODE_REMOVE);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(null);
        }
    }

    private final ListenableAsyncTask.AsyncTaskListener<Void,Void,List<TaskData>> mSaveListener =
            new ListenableAsyncTask.SimpleAsyncTaskListener<Void,Void,List<TaskData>>(){
        @Override
        public void onResult(List<TaskData> data) {
            mViewModel.getAsyncTaskManaget().removeTask(CODE_SAVE);
            onMultipleSaveSuccessful(data);
        }

        @Override
        public void onError(@NonNull Exception error) {
            mViewModel.getAsyncTaskManaget().removeTask(CODE_SAVE);
            onSaveError(error);
        }
    };

    private final ListenableAsyncTask.AsyncTaskListener<Void,Void,List<TaskData>> mRemoveListener =
            new ListenableAsyncTask.SimpleAsyncTaskListener<Void,Void,List<TaskData>>(){
        @Override
        public void onResult(List<TaskData> data) {
            mViewModel.getAsyncTaskManaget().removeTask(CODE_REMOVE);
            onMultipleRemoveSuccessful(data);
        }

        @Override
        public void onError(@NonNull Exception error) {
            mViewModel.getAsyncTaskManaget().removeTask(CODE_REMOVE);
            onRemoveError(error);
        }
    };
}