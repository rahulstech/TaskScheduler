package rahulstech.android.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rahulstech.android.database.datatype.TaskState;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.ui.R;
import rahulstech.android.ui.adapter.TaskListAdapter;
import rahulstech.android.ui.listener.OnClickItemOrChildListener;
import rahulstech.android.ui.viewmodel.TaskListViewModel;
import rahulstech.android.util.time.DateTime;

@SuppressWarnings(value = {"unused"})
public class TaskListActivity extends AppCompatActivity {

    private static final String TAG = "TaskListActivity";

    private static final String KEY_TASK_DATE = "task_date";

    private TaskListViewModel mViewModel;

    private TextView mEmptyLabel;
    private RecyclerView mTasksList;
    private TextView mBtnTaskDate;

    private DateTime mTaskDate;

    private TaskListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(TaskListViewModel.class);

        setContentView(R.layout.activity_task_list);
        mEmptyLabel = findViewById(R.id.empty_label);
        mBtnTaskDate = findViewById(R.id.task_date);
        mTasksList = findViewById(R.id.tasks);

        mBtnTaskDate.setOnClickListener(v->onClickPickTaskDate());
        findViewById(R.id.btn_previous).setOnClickListener(v-> onClickPreviousDate());
        findViewById(R.id.btn_next).setOnClickListener(v->onClickNextDate());
        findViewById(R.id.add_task).setOnClickListener(v->onClickAddTask());
        setTaskDate(DateTime.today());

        mTasksList.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        mAdapter = new TaskListAdapter(this);
        mAdapter.setOnItemOrChildCheckChangeListener((adapter, position, checked, view) -> onTaskCheckedChanged(position,checked));
        mAdapter.setOnClickItemOrChildClickListener(new OnClickItemOrChildListener() {
            @Override
            public void onClickItem(@NonNull RecyclerView.Adapter<?> adapter, int position) {
                onClickTask(position);
            }

            @Override
            public void onClickItemChild(@NonNull RecyclerView.Adapter<?> adapter, int position, @NonNull View child) {}
        });
        mTasksList.setAdapter(mAdapter);

        mViewModel.getAllTasksForDate().observe(this, this::onTasksFetched);
        mViewModel.changeTaskDate(getTaskDate());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        DateTime date = DateTime.parseISODate(savedInstanceState.getString(KEY_TASK_DATE));
        setTaskDate(date);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TASK_DATE,mTaskDate.formatISODate());
    }

    void onClickPickTaskDate() {
        DatePickerDialog picker = new DatePickerDialog(this,(di,year,month,date)->
                onChangeTaskDate(DateTime.ofDate(year,month,date)),
                mTaskDate.getYear(),mTaskDate.getMonth(),mTaskDate.getDate());
        picker.show();
    }

    void onClickPreviousDate() {
        onChangeTaskDate(mTaskDate.addDays(-1));
    }

    void onClickNextDate() {
        onChangeTaskDate(mTaskDate.addDays(1));
    }

    void onChangeTaskDate(@NonNull DateTime newDate) {
        setTaskDate(newDate);
        mViewModel.changeTaskDate(mTaskDate);
    }

    void setTaskDate(@NonNull DateTime date) {
        mTaskDate = date;
        String text = date.format("dd-MMM-yyyy");
        mBtnTaskDate.setText(text);
    }

    DateTime getTaskDate() {
        return mTaskDate;
    }

    void onTasksFetched(List<TaskModel> tasks) {
        mAdapter.setItems(tasks);
        if (null != tasks && !tasks.isEmpty()) {
            mEmptyLabel.setVisibility(View.GONE);
            mTasksList.setVisibility(View.VISIBLE);
        }
        else {
            mTasksList.setVisibility(View.GONE);
            mEmptyLabel.setVisibility(View.VISIBLE);
        }
    }

    void onClickAddTask() {
        Intent i = new Intent(this,CreateOrEditTaskActivity.class);
        i.setAction(CreateOrEditTaskActivity.ACTION_ADD_TASK);
        i.putExtra(CreateOrEditTaskActivity.EXTRA_DATE,mTaskDate.formatISODate());
        startActivity(i);
    }

    void onClickTask(int position) {
        TaskModel task = (TaskModel) mAdapter.getAdapterData(position);
        if (null == task) return;
        Intent i = new Intent(this,ViewTask.class);
        i.putExtra(ViewTask.EXTRA_TASK_ID,task.getId());
        startActivity(i);
    }

    void onTaskCheckedChanged(int adapterPosition, boolean checked) {
        TaskModel model = (TaskModel) mAdapter.getAdapterData(adapterPosition);
        TaskState newState = checked ? TaskState.COMPLETE : TaskState.PENDING;
        mViewModel.changeTaskState(model,newState);
    }
}