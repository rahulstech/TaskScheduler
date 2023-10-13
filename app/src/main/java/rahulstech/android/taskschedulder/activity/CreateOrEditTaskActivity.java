package rahulstech.android.taskschedulder.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.datatype.DBTime;
import rahulstech.android.database.entity.Task;
import rahulstech.android.taskschedulder.R;
import rahulstech.android.taskschedulder.dialog.DialogUtil;
import rahulstech.android.taskschedulder.viewmodel.CreateOrEditTaskViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.text.SpannableTextBuilder;
import rahulstech.android.util.time.DateTime;

import static rahulstech.android.taskschedulder.util.Constant.ACTION_EDIT_TASK;
import static rahulstech.android.taskschedulder.util.Constant.EXTRA_DATE;
import static rahulstech.android.taskschedulder.util.Constant.EXTRA_TASK_ID;

@SuppressWarnings("unused")
public class CreateOrEditTaskActivity extends AppCompatActivity {

    private static final String TAG = CreateOrEditTaskActivity.class.getSimpleName();

    private static final String KEY_HAS_TASK_FETCHED = "key_has_task_fetched";

    private static final String KEY_HAS_PICKED_TIME = "key_has_picked_time";

    private static final String KEY_TASK_DATE = "key_task_date";

    private static final String KEY_TASK_TIME = "key_task_time";

    private boolean mHasTaskFetched = false;
    private DateTime mTaskDate = DateTime.today();
    private boolean mHasPickedTime = false;
    private DateTime mTaskTime = null;
    private Task mTask;

    private TextInputLayout mContainerDescription;
    private EditText mDescription;
    private TextView mTxtDateStart;
    private TextView mTxtTimeStart;

    private CreateOrEditTaskViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_task);

        mViewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(CreateOrEditTaskViewModel.class);

        mContainerDescription = findViewById(R.id.description_container);
        mDescription = findViewById(R.id.task_description);
        mTxtDateStart = findViewById(R.id.task_date_start);
        mTxtTimeStart = findViewById(R.id.task_time);

        setTaskStartDate(getExtraDate(DateTime.today()));
        setTaskStartTime(null);
        findViewById(R.id.btnClear).setOnClickListener(v->onClickClear());
        findViewById(R.id.btnSave).setOnClickListener(v->onClickSaveTask());
        mDescription.setOnClickListener(v -> onClickDescription());
        mTxtDateStart.setOnClickListener(v->onChangeTaskDateStart());
        mTxtTimeStart.setOnClickListener(v->onChangeTaskTimeStart());
        disableInputsForEdit();

        if (isActionEdit()) {
            mViewModel.getTaskById(getExtraTaskId()).observe(this,this::onTaskFetched);
        }
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(this, mSaveListener);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasTaskFetched = savedInstanceState.getInt(KEY_HAS_TASK_FETCHED)==1;
        mHasPickedTime = savedInstanceState.getInt(KEY_HAS_PICKED_TIME)==1;
        mTaskDate = DateTime.parseISODate(savedInstanceState.getString(KEY_TASK_DATE));
        setTaskStartDate(mTaskDate);
        if (mHasPickedTime) {
            mTaskTime = DateTime.parseISOTime(savedInstanceState.getString(KEY_TASK_TIME));
            setTaskStartTime(mTaskTime);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_HAS_TASK_FETCHED,mHasTaskFetched ? 1 : 0);
        outState.putInt(KEY_HAS_PICKED_TIME,mHasPickedTime ? 1 : 0);
        outState.putString(KEY_TASK_DATE,mTaskDate.formatISODate());
        if (mHasPickedTime) {
            outState.putString(KEY_TASK_TIME,mTaskTime.formatISOTime());
        }
    }

    boolean isActionEdit() {
        return ACTION_EDIT_TASK.equals(getIntent().getAction());
    }

    long getExtraTaskId() {
        return getIntent().getLongExtra(EXTRA_TASK_ID,0);
    }

    void disableInputsForEdit() {
        if (isActionEdit()) {
            mTxtDateStart.setVisibility(View.GONE);
        }
    }

    @NonNull
    DateTime getExtraDate(@NonNull DateTime ifNotExists) {
        String value = getIntent().getStringExtra(EXTRA_DATE);
        if (TextUtils.isEmpty(value)) return ifNotExists;
        return DateTime.parseISODate(value);
    }

    void onClickDescription() {
        mContainerDescription.setError(null);
    }

    void onChangeTaskDateStart() {
        DatePickerDialog picker = new DatePickerDialog(this,(di,year,month,date)->{
            mTaskDate = DateTime.ofDate(year,month,date);
            setTaskStartDate(mTaskDate);
        },mTaskDate.getYear(),mTaskDate.getMonth(),mTaskDate.getDate());
        picker.show();
    }

    void onChangeTaskTimeStart() {
        if (mHasPickedTime) {
            DialogUtil.showSingleChoiceDialog(this,null,
                    getResources().getTextArray(R.array.change_task_time_start_options),(di,which)->{
                if (0 == which) {
                    onPickTaskTime();
                }
                else if (1 == which) {
                    onRemoveTaskTime();
                }
            }).show();
        }
        else {
            onPickTaskTime();
        }
    }

    void onPickTaskTime() {
        DateTime time = null == mTaskTime ? DateTime.now() : mTaskTime;
        TimePickerDialog picker = new TimePickerDialog(this,(di,hour,minute)->{
            DateTime newTime = DateTime.ofTime(hour,minute);
            setTaskStartTime(newTime);
            mHasPickedTime = true;
        }, time.getHourOfDay(),time.getMinute(),false);
        picker.show();
    }

    void onRemoveTaskTime() {
        mHasPickedTime = false;
        setTaskStartTime(null);
    }

    void onTaskFetched(Task task) {
        if (null == task) {
            Toast.makeText(this,R.string.message_error_task_not_found,Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            setTask(task);
            mHasTaskFetched = true;
        }
    }

    void setTask(@NonNull Task task) {
        mTask = task;
        if (!mHasTaskFetched) {
            mDescription.setText(task.getDescription());
            DBDate startDate = task.getDateStart();
            DBTime startTime = task.getTimeStart();
            DateTime dateStart = DateTime.ofDate(startDate.getYear(), startDate.getMonth(), startDate.getDate());
            setTaskStartDate(dateStart);
            if (null != startTime) {
                DateTime timeStart = DateTime.ofTime(startTime.getHourOfDay(), startTime.getMinute());
                setTaskStartTime(timeStart);
                mHasPickedTime = true;
            } else {
                setTaskStartTime(null);
                mHasPickedTime = false;
            }
        }
    }

    void onClickClear() {
        mDescription.setText(null);
        mContainerDescription.setError(null);
        setTaskStartDate(getExtraDate(DateTime.today()));
    }

    void onClickSaveTask() {
        CharSequence txtDescription = mDescription.getText();
        int maxDescriptionLength = getResources().getInteger(R.integer.max_task_description_length);
        boolean hasError = false;
        if (TextUtils.isEmpty(txtDescription)) {
            hasError = true;
            mContainerDescription.setError(getString(R.string.message_error_no_task_description));
        }
        if (txtDescription.length() > maxDescriptionLength) {
            hasError = true;
            mContainerDescription.setError(getString(R.string.message_error_task_description_overflown));
        }
        if (hasError) {
            Toast.makeText(this,R.string.message_error_input,Toast.LENGTH_SHORT).show();
            return;
        }
        Task task = new Task();
        task.setDescription(txtDescription.toString());
        task.setDateStart(DBDate.of(mTaskDate.getYear(),mTaskDate.getMonth(),mTaskDate.getDate()));
        if (mHasPickedTime) {
            task.setTimeStart(DBTime.of(mTaskTime.getHourOfDay(),mTaskTime.getMinute()));
        }
        saveTask(task);
    }

    void saveTask(@NonNull Task task) {
        if (isActionEdit()) {
            task.setId(getExtraTaskId());
            task.setState(mTask.getState());
            mViewModel.editTask(task);
        }
        else {
            mViewModel.addTask(task);
        }
    }

    void setTaskStartDate(@NonNull DateTime date) {
        mTaskDate = date;
        CharSequence label = SpannableTextBuilder.text(getString(R.string.label_task_start_date)).italic().build();
        CharSequence dateString = SpannableTextBuilder.text(date.format("d-MMM-yyyy")).bold()
                .relativeSize(1.75f)
                .build();
        CharSequence text = new SpannableStringBuilder()
                .append(label).append("\n")
                .append(dateString);
        mTxtDateStart.setText(text);
    }

    void setTaskStartTime(DateTime time) {
        mTaskTime = time;
        CharSequence label = SpannableTextBuilder.text(getString(R.string.label_task_time_start)).italic().build();
        CharSequence dateString;
        if (null == time) {
            dateString = SpannableTextBuilder.text(getString(R.string.message_task_time_not_set)).bold()
                    .relativeSize(1.75f)
                    .build();
        }
        else {
            dateString = SpannableTextBuilder.text(time.format("hh:mm aa")).bold()
                    .relativeSize(1.75f)
                    .build();
        }
        CharSequence text = new SpannableStringBuilder()
                .append(label).append("\n")
                .append(dateString);
        mTxtTimeStart.setText(text);
    }

    void viewTask(long id) {
        Intent i = new Intent(this,ViewTask.class);
        i.putExtra(EXTRA_TASK_ID,id);
        startActivity(i);
    }

    private ListenableAsyncTask.AsyncTaskListener<Void,Void,Task> mSaveListener
            = new ListenableAsyncTask.AsyncTaskListener<Void, Void, Task>() {

        @Override
        public void onResult(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @Nullable Task task) {
            Toast.makeText(CreateOrEditTaskActivity.this, R.string.message_task_saved, Toast.LENGTH_SHORT).show();
            if (!isActionEdit()) {
                viewTask(task.getId());
            }
            finish();
        }

        @Override
        public void onError(@NonNull ListenableAsyncTask<?, ?, ?> asyncTask, @NonNull Exception error) {
            Log.e(TAG,null,error);
            Toast.makeText(CreateOrEditTaskActivity.this,R.string.message_error_task_not_saved,Toast.LENGTH_SHORT).show();
        }
    };
}