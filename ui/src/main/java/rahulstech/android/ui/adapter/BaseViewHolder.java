package rahulstech.android.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("unused")
public abstract class BaseViewHolder<I> extends RecyclerView.ViewHolder {

    private Context mContext;
    private RecyclerView.Adapter<?> mAdapter;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    public void setAdapter(RecyclerView.Adapter<?> mAdapter) {
        this.mAdapter = mAdapter;
    }

    public RecyclerView.Adapter<?> getAdapter() {
        return mAdapter;
    }

    @Nullable
    public <V extends View> V findViewById(@IdRes int id) {
        return itemView.findViewById(id);
    }

    public final void bind(@Nullable I data) {
        if (null == data) {
            bindNull();
        }
        else {
            bindNonNull(data);
        }
    }

    protected abstract void bindNull();

    protected abstract void bindNonNull(@NonNull I data);
}

