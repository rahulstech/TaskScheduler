package rahulstech.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import rahulstech.android.database.datatype.TaskState;
import rahulstech.android.database.model.TaskModel;
import rahulstech.android.ui.R;
import rahulstech.android.ui.listener.OnClickItemOrChildListener;
import rahulstech.android.ui.listener.OnItemCheckChangeListener;
import rahulstech.android.ui.util.TextUtil;

@SuppressWarnings(value = {"unused"})
public class TaskListAdapter extends ListAdapter<TaskModel, RecyclerView.ViewHolder> {

    private static final String TAG = "TaskListAdapter";

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_TASK = 2;

    private static final DiffUtil.ItemCallback<TaskModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<TaskModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull TaskModel oldItem, @NonNull TaskModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskModel oldItem, @NonNull TaskModel newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final Comparator<TaskModel> mStateComparator = (l,r) -> {
        int lCode = getStateCode(l.getState());
        int rCode = getStateCode(r.getState());
        return lCode-rCode;
    };

    private final Context mContext;
    private final LayoutInflater mInflater;

    //private List<String> mSections;
    //private List<Integer> mSectionPositions;

    private OnItemCheckChangeListener mItemCheckListener;

    private OnClickItemOrChildListener mItemOrChildCLickListener;

    public TaskListAdapter(@NonNull Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemCheckChangeListener(OnItemCheckChangeListener listener) {
        mItemCheckListener = listener;
    }

    public void setOnClickItemOrChildClickListener(OnClickItemOrChildListener listener) {
        this.mItemOrChildCLickListener = listener;
    }

    @Override
    public void submitList(@Nullable List<TaskModel> list) {
        List<TaskModel> sortedList = sort(list);
        //createSections(sortedList);
        super.submitList(sortedList);
    }

    public List<TaskModel> sort(List<TaskModel> list) {
        if (null == list) return null;
        Collections.sort(list,mStateComparator);
        return list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*if (TYPE_TASK == viewType) {
            View itemView = mInflater.inflate(R.layout.task_list_item,parent,false);
            return new TaskViewHolder(itemView);
        }
        else {
            View headerView = mInflater.inflate(R.layout.task_list_section_header,parent,false);
            return new HeaderViewHolder(headerView);
        }*/
        View itemView = mInflater.inflate(R.layout.task_list_item,parent,false);
        TaskViewHolder holder = new TaskViewHolder(this,itemView);
        holder.setOnItemCheckChangeListener(mItemCheckListener);
        holder.setOnClickItemOrChildClickListener(mItemOrChildCLickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*int viewType = getItemViewType(position);
        if (TYPE_HEADER == viewType) {
            int section = getSectionForPosition(position);
            ((HeaderViewHolder) holder).bind(getSections().get(section));
        }
        else {
            TaskModel model = getItem(position);
            ((TaskViewHolder) holder).bind(model);
        }*/
        TaskModel model = getItem(position);
        ((TaskViewHolder) holder).bind(model);
    }

    @Nullable
    public TaskModel getItem(int position) {
        return getCurrentList().get(position);
    }

    /*@Override
    public int getItemCount() {
        return getCurrentList().size()+getSections().size();
    }*/

    /*@Override
    public int getItemViewType(int position) {
        if (getSectionPositions().contains(position)) {
            return TYPE_HEADER;
        }
        return TYPE_TASK;
    }*/

    private int getStateCode(@NonNull TaskState state) {
        switch (state) {
            case START: return 0;
            case PAUSE: return 1;
            case COMPLETE: return 3;
            case CANCEL: return 4;
            default: return 2;
        }
    }

    /*
    protected void createSections(List<TaskModel> org) {
        List<String> sections = new ArrayList<>();
        List<Integer> positions = new ArrayList<>();
        String last = null;
        int position = 0;
        for (TaskModel i : org) {
            String section = onCreateSection(i);
            if (!Objects.equals(last,section)) {
                sections.add(section);
                positions.add(position);
                last = section;
                position++;
            }
            position++;
        }
        setSections(sections);
        setSectionPositions(positions);
    }

    protected String onCreateSection(@NonNull TaskModel task) {
        return task.getState().name();
    }

    public List<String> getSections() {
        return mSections;
    }

    public void setSections(List<String> sections) {
        mSections = sections;
    }

    public List<Integer> getSectionPositions() {
        return mSectionPositions;
    }

    public void setSectionPositions(List<Integer> positions) {
        mSectionPositions = positions;
    }

    public int getSectionForPosition(int position) {
        return getSectionPositions().indexOf(position);
    }
     */


    public static class TaskViewHolder extends ClickableViewHolder<TaskModel> {

        TextView mDescription;
        TextView mDetails;
        CheckBox mCheckBox;

        CompoundButton.OnCheckedChangeListener mCBCheckChange = this::onCheckChanged;

        public TaskViewHolder(@NonNull RecyclerView.Adapter<?> adapter, @NonNull View itemView) {
            super(adapter,itemView);
            mDescription = itemView.findViewById(R.id.text1);
            mDetails = itemView.findViewById(R.id.text2);
            mCheckBox = itemView.findViewById(R.id.checkbox);
            mCheckBox.setOnCheckedChangeListener(mCBCheckChange);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        protected void bindNull() {
            mDescription.setText(null);
            mDetails.setText(null);
            setCheckSilently(false);
        }

        @Override
        protected void bindNonNull(@NonNull TaskModel model) {
            TaskState state = model.getState();
            if (state.equals(TaskState.COMPLETE)) {
                mDescription.setText(TextUtil.strikeThrough(model.getDescription()));
                setCheckSilently(true);
            }
            else {
                mDescription.setText(model.getDescription());
                setCheckSilently(false);
            }
            // TODO: add task details
        }

        void setCheckSilently(boolean checked) {
            mCheckBox.setOnCheckedChangeListener(null);
            mCheckBox.setChecked(checked);
            mCheckBox.setOnCheckedChangeListener(mCBCheckChange);
        }
    }

    /**
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView mHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            mHeader = itemView.findViewById(R.id.header_text);
        }

        public void bind(String value) {
            mHeader.setText(value);
        }
    }
    */
}
