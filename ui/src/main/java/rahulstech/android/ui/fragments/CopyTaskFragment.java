package rahulstech.android.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import rahulstech.android.database.entity.Task;
import rahulstech.android.ui.R;
import rahulstech.android.ui.viewmodel.TaskViewModel;

public class CopyTaskFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CopyTaskActivity";

    public static final String EXTRA_ID = "extra_id";

    private TaskViewModel mViewModel;

    private Spinner mCopyFrequency;

    private TextView mDateExact;

    private TextView mDateStart;

    private TextView mDateEnd;

    private EditText mInterval;

    private Spinner mIntervalUnit;

    private ViewGroup mContainerRepeatSelected;

    private ViewGroup mContainerRepeatRegular;

    private Task mTask;

    public CopyTaskFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = new ViewModelProvider(requireActivity(),
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(TaskViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_copy_task,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCopyFrequency = findViewById(R.id.choose_copy_task_frequency);
        mContainerRepeatSelected = findViewById(R.id.container_repeat_selected);
        mDateExact = findViewById(R.id.task_date);
        mContainerRepeatRegular = findViewById(R.id.container_repeat_regular);
        mDateStart = findViewById(R.id.task_date_start);
        mDateEnd = findViewById(R.id.task_date_end);
        mInterval = findViewById(R.id.interval);
        mIntervalUnit = findViewById(R.id.choose_interval_unit);

        mCopyFrequency.setOnItemSelectedListener(this);
        mIntervalUnit.setOnItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        long taskId = getExtraTaskId();
        if (taskId <= 0) {
            showTaskNotFoundAndExit();
        }
        mViewModel.getTaskById(taskId).observe(this,this::onTaskFetched);
    }

    @Nullable
    public <V extends View> V findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mCopyFrequency == parent) {
            onChangeCopyFrequency(position);
        }
        else if (mIntervalUnit == parent) {
            onChangeIntervalUnit(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public long getExtraTaskId() {
        return getArguments().getLong(EXTRA_ID,0);
    }

    private void showTaskNotFoundAndExit() {
        Toast.makeText(requireContext(),R.string.message_error_task_not_found,Toast.LENGTH_SHORT).show();
        // TODO: exit
    }

    private void onTaskFetched(Task task) {
        if (null == task) {
            showTaskNotFoundAndExit();
        }
        else {
            mTask = task;
        }
    }

    private void onChangeCopyFrequency(int which) {
        if (0 == which) {
            mContainerRepeatRegular.setVisibility(View.GONE);
            mContainerRepeatSelected.setVisibility(View.VISIBLE);
        }
        else {
            mContainerRepeatSelected.setVisibility(View.GONE);
            mContainerRepeatRegular.setVisibility(View.VISIBLE);
        }
    }

    private void onChangeIntervalUnit(int which) {

    }
}