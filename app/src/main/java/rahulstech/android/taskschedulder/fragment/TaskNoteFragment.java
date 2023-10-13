package rahulstech.android.taskschedulder.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import rahulstech.android.database.entity.Task;
import rahulstech.android.taskschedulder.R;
import rahulstech.android.taskschedulder.viewmodel.TaskNoteFragmentViewModel;
import rahulstech.android.taskschedulder.viewmodel.TaskReminderFragmentViewModel;
import rahulstech.android.util.concurrent.ListenableAsyncTask;

import static rahulstech.android.taskschedulder.util.Constant.EXTRA_TASK_ID;

@SuppressWarnings({"unused","FieldMayBeFinal"})
public class TaskNoteFragment extends Fragment {

    private static final String TAG = TaskReminderFragment.class.getSimpleName();

    private static final String KEY_HAS_TASK_FETCHED = "key_has_task_fetched";

    private TaskNoteFragmentViewModel mViewModel;
    private NavController mNavController;

    private EditText mNote;

    private boolean mHasTaskFetched = false;

    public TaskNoteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory())
                .get(TaskNoteFragmentViewModel.class);
        mViewModel.getAsyncTaskManager().getTaskUpdateLiveData().observe(this,mSaveNoteListener);
        mViewModel.getTaskById(getExtraTaskId()).observe(this,this::onTaskFetched);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(view);
        mNote = view.findViewById(R.id.input_note);
        view.findViewById(R.id.btnClear).setOnClickListener(v->onClickClear());
        view.findViewById(R.id.btnSave).setOnClickListener(v->onClickSave());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState) {
            mHasTaskFetched = savedInstanceState.getBoolean(KEY_HAS_TASK_FETCHED);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setTitle(R.string.label_task_note);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_TASK_FETCHED,mHasTaskFetched);
    }

    public long getExtraTaskId() {
        return requireArguments().getLong(EXTRA_TASK_ID,0L);
    }

    private void onTaskFetched(Task task) {
        if (null == task) {
            showNotFoundAndExit();
        }
        else {
            setNote(task.getNote());
            mHasTaskFetched = true;
        }
    }

    private void setNote(@Nullable String note) {
        if (!mHasTaskFetched) {
            mNote.setText(note);
        }
    }

    private void onClickClear() {
        mNote.setText(null);
    }

    private void onClickSave() {
        String note = mNote.getText().toString();
        Task newValues = new Task();
        newValues.setId(getExtraTaskId());
        if (TextUtils.isEmpty(note)) {
            newValues.setNote(null);
        }
        else {
            newValues.setNote(note);
        }
        mViewModel.saveNote(newValues);
    }

    private void showNotFoundAndExit() {
        Toast.makeText(requireContext(),R.string.message_error_task_not_found,Toast.LENGTH_SHORT).show();
        mNavController.popBackStack();
    }

    private ListenableAsyncTask.AsyncTaskListener mSaveNoteListener = new ListenableAsyncTask.AsyncTaskListener() {

        @Override
        public void onResult(@NonNull ListenableAsyncTask asyncTask, @Nullable Object o) {
            Toast.makeText(requireContext(),R.string.message_changes_saved,Toast.LENGTH_SHORT).show();
            mNavController.popBackStack();
        }

        @Override
        public void onError(@NonNull ListenableAsyncTask asyncTask, @NonNull Exception error) {
            Log.e(TAG,null,error);
            Toast.makeText(requireContext(),R.string.message_changes_not_saved,Toast.LENGTH_SHORT).show();
        }
    };
}