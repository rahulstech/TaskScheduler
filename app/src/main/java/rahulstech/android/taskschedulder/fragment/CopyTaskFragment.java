package rahulstech.android.taskschedulder.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.taskschedulder.R;
import rahulstech.android.taskschedulder.viewmodel.CopyTaskFragmentViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;
import rahulstech.android.util.text.SpannableTextBuilder;

import static rahulstech.android.taskschedulder.util.Constant.EXTRA_TASK_ID;

@SuppressWarnings("unused")
public class CopyTaskFragment extends Fragment {

    private static final String TAG = "CopyTaskActivity";

    private static final String KEY_HAS_TASK_FETCHED = "key_has_task_fetched";

    private CopyTaskFragmentViewModel mViewModel;
    private NavController mNavController;

    private ViewGroup mLoadingScreen;
    private ViewGroup mContent;
    private Spinner mChooseRepeat;
    private ViewGroup mContainerRegular;
    private TextView mRangeStart;
    private TextView mRangeEnd;
    private EditText mInterval;
    private Spinner mUnit;

    private boolean mHasTaskFetched = false;
    private TaskModel mTask;

    private LocalDate mPickedRangeStart;
    private LocalDate mPickedRangeEnd;

    public CopyTaskFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this,(ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(CopyTaskFragmentViewModel.class);
        mViewModel.getTaskById(getExtraTaskId()).observe(this,this::onTaskFetched);
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(this,mSaveListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_copy_task,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(view);
        mLoadingScreen = view.findViewById(R.id.loading_view);
        mContent = view.findViewById(R.id.content);
        mChooseRepeat = view.findViewById(R.id.choose_repeat);
        mContainerRegular = view.findViewById(R.id.container_regular_interval);
        mRangeStart = view.findViewById(R.id.range_start);
        mRangeEnd = view.findViewById(R.id.range_end);
        mInterval = view.findViewById(R.id.interval);
        mUnit = view.findViewById(R.id.choose_unit);
        mChooseRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onChangeTaskRepeat(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mRangeStart.setOnClickListener(v->onClickRangeStart());
        mRangeEnd.setOnClickListener(v->onClickRangeEnd());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState) {
            mHasTaskFetched = savedInstanceState.getBoolean(KEY_HAS_TASK_FETCHED,false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setTitle(R.string.label_copy_task);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_TASK_FETCHED,mHasTaskFetched);
    }

    public long getExtraTaskId() {
        return requireArguments().getLong(EXTRA_TASK_ID,0);
    }

    private void onTaskFetched(TaskModel task) {
        if (null == task) {
            showTaskNotFoundAndExit();
        }
        else {
            setTask(task);
            mHasTaskFetched = true;
        }
    }

    private void setTask(@NonNull TaskModel task) {
        mLoadingScreen.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    private void onClickRangeStart() {
        LocalDate date = null == mPickedRangeStart ? LocalDate.now() : mPickedRangeStart;
        DatePickerDialog picker = new DatePickerDialog(requireContext(),(view, year, month, dayOfMonth) -> {
            LocalDate newDate = LocalDate.of(year,month,dayOfMonth);
            setRangeStart(newDate);
        },
                date.getYear(),date.getMonthValue(),date.getDayOfMonth());
        LocalDate min = getMinRange();
        LocalDate max = getMaxRange(getMinRange());
        picker.getDatePicker().setMinDate(min.toEpochDay());
        picker.getDatePicker().setMaxDate(max.toEpochDay());
        picker.show();
    }

    private void onClickRangeEnd() {
        LocalDate date = null == mPickedRangeEnd ? LocalDate.now() : mPickedRangeEnd;
        DatePickerDialog picker = new DatePickerDialog(requireContext(),(view, year, month, dayOfMonth) -> {
            LocalDate newDate = LocalDate.of(year,month,dayOfMonth);
            setRangeEnd(newDate);
        },
                date.getYear(),date.getMonthValue(),date.getDayOfMonth());
        LocalDate min = getMinRange();
        LocalDate max = getMaxRange(getMinRange());
        picker.getDatePicker().setMinDate(min.toEpochDay());
        picker.getDatePicker().setMaxDate(max.toEpochDay());
        picker.show();
    }

    @NonNull
    private LocalDate getMinRange() {
        // TODO: need to check range min
        return null == mPickedRangeStart ? mTask.getDate() : mPickedRangeStart;
    }

    @NonNull
    private LocalDate getMaxRange(@NonNull LocalDate min) {
        int selection = mChooseRepeat.getSelectedItemPosition();
        if (0 == selection) return min;
        if (selection < 9)
        switch (selection) {
            case 3:
            case 4:
            case 5: return min.plusYears(5);
            case 6:
            case 7: return min.plusYears(10);
            case 8: return min.plusYears(80);
            default: return min.plusYears(1);
        }
        int unitSelection = mUnit.getSelectedItemPosition();
        switch (unitSelection) {
            default: return min.plusYears(1);
            case 1: return min.plusYears(5);
            case 2: return min.plusYears(10);
            case 3: return min.plusYears(80);
        }
    }

    private void setRangeStart(@NonNull LocalDate date) {
        setRangeText(mRangeStart,getText(R.string.label_range_start),date);
    }

    private void setRangeEnd(@NonNull LocalDate date) {
        setRangeText(mRangeEnd,getText(R.string.label_range_end),date);
    }

    private void setRangeText(@NonNull TextView view, @NonNull CharSequence label, @NonNull LocalDate date) {
        CharSequence dateFormatted = date.format(DateTimeFormatter.ofPattern("d-MMM-yy"));
        CharSequence text = new SpannableStringBuilder()
                .append(SpannableTextBuilder.text(label).relativeSize(.75f).build())
                .append("\n")
                .append(SpannableTextBuilder.text(dateFormatted).bold().relativeSize(1.5f).build());
        view.setText(text);
    }

    private void onChangeTaskRepeat(int which) {
        if (which == 0) {
            toggleShowContentRegularInterval(false);
        }
        else if (which == 9) {
            toggleShowContentRegularInterval(true);
            toggleShowContentCustomIntervalInput(true);
        }
        else {
            toggleShowContentCustomIntervalInput(true);
        }
    }

    private void toggleShowContentRegularInterval(boolean show) {
        mContainerRegular.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void toggleShowContentCustomIntervalInput(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mInterval.setVisibility(visibility);
        mUnit.setVisibility(visibility);
    }

    private void showTaskNotFoundAndExit() {
        Toast.makeText(requireContext(),R.string.message_error_task_not_found,Toast.LENGTH_SHORT).show();
        mNavController.popBackStack();
    }

    private ListenableAsyncTask.AsyncTaskListener mSaveListener = new ListenableAsyncTask.AsyncTaskListener(){

        @Override
        public void onResult(@NonNull ListenableAsyncTask asyncTask, @Nullable Object o) {

        }

        @Override
        public void onError(@NonNull ListenableAsyncTask asyncTask, @NonNull Exception error) {

        }
    };
}