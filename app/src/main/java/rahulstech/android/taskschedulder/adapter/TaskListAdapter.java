package rahulstech.android.taskschedulder.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rahulstech.android.database.datatype.TaskState;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.taskschedulder.R;
import rahulstech.android.taskschedulder.listener.OnClickItemOrChildListener;
import rahulstech.android.taskschedulder.listener.OnItemOrChildCheckChangeListener;
import rahulstech.android.util.text.SpannableTextBuilder;

@SuppressWarnings(value = {"unused"})
public class TaskListAdapter
        extends SectionedAdapter<TaskListAdapter.Header,TaskModel,
        TaskListAdapter.HeaderViewHolder, TaskListAdapter.TaskViewHolder> {

    private static final String TAG = "TaskListAdapter";

    private final Comparator<TaskModel> mStateComparator = (l,r) -> {
        int lCode = getStateCode(l.getState());
        int rCode = getStateCode(r.getState());
        return lCode-rCode;
    };

    private OnItemOrChildCheckChangeListener mCheckListener;

    private OnClickItemOrChildListener mCLickListener;

    public TaskListAdapter(@NonNull Context context) {
        super(context);
    }

    public void setOnItemOrChildCheckChangeListener(OnItemOrChildCheckChangeListener listener) {
        mCheckListener = listener;
    }

    public void setOnClickItemOrChildClickListener(OnClickItemOrChildListener listener) {
        this.mCLickListener = listener;
    }

    private List<TaskModel> sort(List<TaskModel> list) {
        if (null == list) return null;
        Collections.sort(list,mStateComparator);
        return list;
    }

    @NonNull
    @Override
    protected HeaderViewHolder onCreateHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View headerView = getLayoutInflater().inflate(R.layout.task_list_section_header,parent,false);
        return new HeaderViewHolder(headerView);

    }

    @NonNull
    @Override
    protected TaskViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = getLayoutInflater().inflate(R.layout.task_list_item,parent,false);
        return new TaskViewHolder(this,itemView);

    }

    @Override
    protected void onBindHeaderViewHolder(@NonNull HeaderViewHolder holder, int adapterPosition) {
        Header data = (Header) getAdapterData(adapterPosition);
        holder.bind(data);
    }

    @Override
    protected void onBindChildViewHolder(@NonNull TaskViewHolder holder, int adapterPosition) {
        TaskModel model = (TaskModel) getAdapterData(adapterPosition);
        holder.setOnClickItemOrChildClickListener(mCLickListener);
        holder.setOnItemOrChildCheckChangeListener(mCheckListener);
        holder.bind(model);
    }

    @Override
    protected void runInBackgroundBeforeBuildHeader(@NonNull List<TaskModel> items) {
        sort(items);
    }

    @NonNull
    @Override
    protected Header onBuildSectionHeader(@NonNull TaskModel item) {
        TaskState state = item.getState();
        String displayText;
        switch (state) {
            case COMPLETE: displayText = getContext().getString(R.string.label_state_complete);
            break;
            case CANCEL: displayText = getContext().getString(R.string.label_state_cancel);
            break;
            default: displayText = getContext().getString(R.string.label_state_pending);
        }
        return new Header(displayText,state);
    }

    private int getStateCode(@NonNull TaskState state) {
        switch (state) {
            case COMPLETE: return 1;
            case CANCEL: return 2;
            default: return 0;
        }
    }

    public static class TaskViewHolder extends ClickableViewHolder<TaskModel> {

        TextView mDescription;
        CheckBox mCheckBox;

        CompoundButton.OnCheckedChangeListener mCBCheckChange = this::onCheckChanged;

        public TaskViewHolder(@NonNull RecyclerView.Adapter<?> adapter, @NonNull View itemView) {
            super(adapter,itemView);
            mDescription = itemView.findViewById(R.id.text1);
            mCheckBox = itemView.findViewById(R.id.checkbox);
            mCheckBox.setOnCheckedChangeListener(mCBCheckChange);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        protected void bindNull() {
            mDescription.setText(null);
            setCheckSilently(false);
        }

        @Override
        protected void bindNonNull(@NonNull TaskModel model) {
            TaskState state = model.getState();
            if (TaskState.CANCEL == state) {
                mDescription.setEnabled(false);
                enableCheckBox(false);
            }
            else if (TaskState.COMPLETE == state) {
                mDescription.setText(SpannableTextBuilder.text(model.getDescription()).strikeThrough().build());
                mDescription.setEnabled(false);
                setCheckSilently(true);
                enableCheckBox(true);
            }
            else {
                mDescription.setText(SpannableTextBuilder.text(model.getDescription()).bold().build());
                mDescription.setEnabled(true);
                setCheckSilently(false);
                enableCheckBox(true);
            }
        }

        void enableCheckBox(boolean enable) {
            mCheckBox.setEnabled(enable);
            mCheckBox.setClickable(enable);
        }

        void setCheckSilently(boolean checked) {
            mCheckBox.setOnCheckedChangeListener(null);
            mCheckBox.setChecked(checked);
            mCheckBox.setOnCheckedChangeListener(mCBCheckChange);
        }
    }

    public static class Header {

        private String displayText;
        private TaskState taskState;

        public Header(String displayText, TaskState taskState) {
            this.displayText = displayText;
            this.taskState = taskState;
        }

        public String getDisplayText() {
            return displayText;
        }

        public TaskState getTaskState() {
            return taskState;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Header)) return false;
            Header header = (Header) o;
            return taskState == header.taskState;
        }

        @Override
        public int hashCode() {
            return Objects.hash(taskState);
        }
    }

    public static class HeaderViewHolder extends BaseViewHolder<Header> {

        TextView mHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            mHeader = findViewById(R.id.header_text);
        }

        @Override
        protected void bindNull() {
            mHeader.setText(null);
        }

        @Override
        protected void bindNonNull(@NonNull Header data) {
            mHeader.setText(data.displayText);
            if (data.getTaskState() == TaskState.PENDING) {
                mHeader.setEnabled(true);
            }
            else {
                mHeader.setEnabled(false);
            }
        }
    }
}
