package rahulstech.android.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import rahulstech.android.database.datatype.DBDate;
import rahulstech.android.database.entity.Task;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.ui.R;
import rahulstech.android.ui.viewmodel.CreateOrEditTaskViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.time.DateTime;

@SuppressWarnings("unused")
public class CreateOrEditTaskActivity extends AppCompatActivity {

    private static final String TAG = CreateOrEditTaskActivity.class.getSimpleName();

    private static final String KEY_SAVED_STATE = "key_saved_state";

    private static final int TASK_CODE = 1;

    public static final String ACTION_ADD_TASK = "action_add_task";

    public static final String ACTION_EDIT_TASK = "action_edit_task";

    public static final String EXTRA_DATE = "date";

    public static final String EXTRA_TASK_ID = "id";

    private boolean mHasTaskFetched = false;
    private DateTime mTaskDate = null;

    private TextInputLayout mContainerDescription;
    private EditText mDescription;
    private TextView mTxtDateStart;

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

        findViewById(R.id.btnClear).setOnClickListener(v->onClickClear());
        findViewById(R.id.btnSave).setOnClickListener(v->onClickSaveTask());
        mDescription.setOnClickListener(v -> onClickDescription());
        mTxtDateStart.setOnClickListener(v->onChangeTaskDateStart());

        disableInputsForEdit();
        setTaskStartDate(getExtraDate(DateTime.today()));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SavedState state = savedInstanceState.getParcelable(KEY_SAVED_STATE);
        mHasTaskFetched = state.mHasFetched;
        setTaskStartDate(state.mTaskDate);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SavedState state = new SavedState();
        state.mHasFetched = mHasTaskFetched;
        state.mTaskDate = mTaskDate;
        outState.putParcelable(KEY_SAVED_STATE,state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isActionEdit() && !mHasTaskFetched) {
            mViewModel.getTaskById(getExtraTaskId()).observe(this,this::onTaskFetched);
        }
        ListenableAsyncTask<Void,Void,Task> asyncTask = mViewModel.getAsyncTaskManager().getTask(TASK_CODE);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(mAsyncTaskLister);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ListenableAsyncTask<Void,Void,Task> asyncTask = mViewModel.getAsyncTaskManager().getTask(TASK_CODE);
        if (null != asyncTask) {
            asyncTask.setAsyncTaskLister(null);
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

    void onTaskFetched(TaskModel task) {
        if (null == task) {
            Toast.makeText(this,R.string.message_error_task_not_found,Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            setTask(task);
        }
        mHasTaskFetched = true;
    }

    void setTask(@NonNull TaskModel task) {
        mDescription.setText(task.getDescription());
        DBDate startDate = task.getDateStart();
        DateTime dateStart = DateTime.ofDate(startDate.getYear(),startDate.getMonth(),startDate.getDate());
        setTaskStartDate(dateStart);
    }

    void onClickClear() {
        mDescription.setText(null);
        mContainerDescription.setError(null);
        DateTime date = getExtraDate(DateTime.today());
        setTaskStartDate(date);
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
            Toast.makeText(this,R.string.message_has_error,Toast.LENGTH_SHORT).show();
            return;
        }
        Task task = new Task();
        task.setDescription(txtDescription.toString());
        task.setDateStart(DBDate.of(mTaskDate.getYear(),mTaskDate.getMonth(),mTaskDate.getDate()));
        saveTask(task);
    }

    void saveTask(@NonNull Task task) {
        ListenableAsyncTask<Void,Void,Task> asyncTask;
        if (isActionEdit()) {
            task.setId(getExtraTaskId());
            asyncTask = mViewModel.editTask(task);
        }
        else {
            asyncTask = mViewModel.addTask(task);
        }
        asyncTask.setAsyncTaskLister(mAsyncTaskLister);
        asyncTask.start(TASK_CODE);
    }

    void setTaskStartDate(@NonNull DateTime date) {
        mTaskDate = date;
        String label = getString(R.string.label_task_start_date);
        String dateString = date.format("d-MMM-yy");
        SpannableString text = new SpannableString(label+"\n"+dateString);
        text.setSpan(new RelativeSizeSpan(.75f),0,label.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        text.setSpan(new StyleSpan(Typeface.BOLD),label.length(),text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTxtDateStart.setText(text);
    }

    void viewTask(long id) {
        Intent i = new Intent(this,ViewTask.class);
        i.putExtra(ViewTask.EXTRA_TASK_ID,id);
        startActivity(i);
    }

    ListenableAsyncTask.AsyncTaskListener<Void,Void,Task> mAsyncTaskLister = new ListenableAsyncTask.SimpleAsyncTaskListener<Void, Void, Task>() {

        @Override
        public void onResult(Task task) {
            mViewModel.getAsyncTaskManager().removeTask(TASK_CODE);
            Toast.makeText(CreateOrEditTaskActivity.this, R.string.message_task_saved, Toast.LENGTH_SHORT).show();
            if (!isActionEdit()) {
                viewTask(task.getId());
            }
            finish();
        }

        @Override
        public void onError(@NonNull Exception error) {
            mViewModel.getAsyncTaskManager().removeTask(TASK_CODE);
            Toast.makeText(CreateOrEditTaskActivity.this,R.string.message_error_task_not_saved,Toast.LENGTH_SHORT).show();
        }
    };

    public static class SavedState extends View.BaseSavedState {

        boolean mHasFetched;
        DateTime mTaskDate;

        public SavedState() {
            super(EMPTY_STATE);
        }

        protected SavedState(Parcel source) {
            super(source);
            if (1 == source.readInt()) {
                mTaskDate = DateTime.parseISODate(source.readString());
            }
            mHasFetched = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            if (null != mTaskDate) {
                out.writeInt(1);
                out.writeString(mTaskDate.formatISODate());
            }
            else {
                out.writeInt(0);
            }
            out.writeInt(mHasFetched ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}