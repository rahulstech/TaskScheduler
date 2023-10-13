package rahulstech.android.taskschedulder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import rahulstech.android.util.concurrent.AppExecutors;

@SuppressWarnings({"unused","FieldMayBeFinal","unchecked"})
public abstract class SectionedAdapter<H, C,HVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SectionedAdapter";

    public static final int TYPE_HEADER = 1;

    public static final int TYPE_CHILD = 2;

    private Context mContext;
    private LayoutInflater mInflater;

    private List<H> mHeaders = new ArrayList<>();
    private List<C> mChildren = new ArrayList<>();
    private List<PositionData> mPositions = new ArrayList<>();
    private SectionHeaderBuilderTask mTask = null;

    public SectionedAdapter(@NonNull Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    @NonNull
    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    public void setItems(@Nullable List<C> items) {
        if (null != mTask) {
            mTask.cancel(true);
        }
        mTask = new SectionHeaderBuilderTask();
        mTask.executeOnExecutor(AppExecutors.getBackgroundExecutor(),items);
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_HEADER == viewType) {
            return onCreateHeaderViewHolder(parent,viewType);
        }
        else {
            return onCreateChildViewHolder(parent,viewType);
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == TYPE_HEADER) {
            onBindHeaderViewHolder((HVH) holder,position);
        }
        else {
            onBindChildViewHolder((CVH) holder,position);
        }
    }

    @NonNull
    protected abstract HVH onCreateHeaderViewHolder(@NonNull ViewGroup parent, int viewType);

    @NonNull
    protected abstract CVH onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType);

    protected abstract void onBindHeaderViewHolder(@NonNull HVH holder, int adapterPosition);

    protected abstract void onBindChildViewHolder(@NonNull CVH holder, int adapterPosition);

    public PositionData getPositionData(int adapterPosition) {
        return mPositions.get(adapterPosition);
    }

    @Override
    public int getItemCount() {
        return mPositions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPositions.get(position).itemType;
    }

    public H getHeader(int section) {
        return mHeaders.get(section);
    }

    @NonNull
    public List<H> getHeaders() {
        return Collections.unmodifiableList(mHeaders);
    }

    public C getChild(int position) {
        return mChildren.get(position);
    }

    public Object getAdapterData(int adapterPosition) {
        PositionData pData = mPositions.get(adapterPosition);
        if (pData.itemType == TYPE_HEADER) {
            return getHeader(pData.section);
        }
        return getChild(pData.position);
    }

    protected void runInBackgroundBeforeBuildHeader(@NonNull List<C> items) {}

    @NonNull
    protected abstract H onBuildSectionHeader(@NonNull C item);

    @SuppressLint("NotifyDataSetChanged")
    private void onHandleTaskResult(@Nullable TaskResult result) {
        mHeaders.clear();
        mChildren.clear();
        mPositions.clear();
        if (null != result) {
            mHeaders.addAll(result.headers);
            mChildren.addAll(result.children);
            mPositions.addAll(result.positions);
        }
        notifyDataSetChanged();
    }

    public static class PositionData {
        int itemType;
        int position;
        int section;
    }

    @SuppressLint("StaticFieldLeak")
    private class SectionHeaderBuilderTask extends AsyncTask<List<C>,Void,TaskResult> {

        public SectionHeaderBuilderTask() {
            super();
        }

        @SafeVarargs
        @Override
        protected final TaskResult doInBackground(List<C>... lists) {
            if (lists.length == 0) return null;
            List<C> items = lists[0];
            if (null == items || items.isEmpty()) return null;
            runInBackgroundBeforeBuildHeader(items);
            return buildHeaders(items);
        }

        private TaskResult buildHeaders(@NonNull List<C> list) {
            ArrayList<H> headers = new ArrayList<>();
            ArrayList<PositionData> positions = new ArrayList<>();
            int position = 0;
            H lastHeader = null;
            for (C item : list) {
                H newHeader = onBuildSectionHeader(item);
                if (!Objects.equals(lastHeader,newHeader)) {
                    headers.add(newHeader);
                    lastHeader = newHeader;
                    PositionData pData = new PositionData();
                    pData.itemType = TYPE_HEADER;
                    pData.section = pData.position = headers.size()-1;
                    positions.add(pData);
                }
                PositionData pData = new PositionData();
                pData.itemType = TYPE_CHILD;
                pData.section = headers.size()-1;
                pData.position = position++;
                positions.add(pData);
            }
            TaskResult result = new TaskResult();
            result.headers = headers;
            result.children = list;
            result.positions = positions;
            return result;
        }

        @Override
        protected void onPostExecute(TaskResult taskResult) {
            onHandleTaskResult(taskResult);
        }

        @Override
        protected void onCancelled() {
            onHandleTaskResult(null);
        }
    }

    private class TaskResult {
        List<H> headers;
        List<C> children;
        List<PositionData> positions;
    }
}
