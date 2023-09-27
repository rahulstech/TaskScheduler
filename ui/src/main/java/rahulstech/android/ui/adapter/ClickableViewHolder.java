package rahulstech.android.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import rahulstech.android.ui.listener.OnClickItemOrChildListener;
import rahulstech.android.ui.listener.OnItemCheckChangeListener;

@SuppressWarnings("unused")
public abstract class ClickableViewHolder<I> extends RecyclerView.ViewHolder {

    private final RecyclerView.Adapter<?> mAdapter;

    private OnClickItemOrChildListener mItemOrChildClick;

    private OnItemCheckChangeListener mItemCheckChange;

    public ClickableViewHolder(@NonNull RecyclerView.Adapter<?> adapter, @NonNull View itemView) {
        super(itemView);
        mAdapter = adapter;
    }

    @NonNull
    public RecyclerView.Adapter<?> getAdapter() {
        return mAdapter;
    }

    public void setOnClickItemOrChildClickListener(OnClickItemOrChildListener listener) {
        mItemOrChildClick = listener;
    }

    public OnClickItemOrChildListener getOnClickItemOrChildListener() {
        return mItemOrChildClick;
    }

    public OnItemCheckChangeListener getOnItemCheckChangeListener() {
        return mItemCheckChange;
    }

    public void setOnItemCheckChangeListener(OnItemCheckChangeListener listener) {
        mItemCheckChange = listener;
    }

    public void bind(@Nullable I item) {
        if (null == item) {
            bindNull();
        }
        else {
            bindNonNull(item);
        }
    }

    protected abstract void bindNull();

    protected abstract void bindNonNull(@NonNull I item);

    protected void onClick(View v) {
        if (null == mItemOrChildClick) return;
        if (itemView == v) {
            mItemOrChildClick.onClickItem(getAdapter(),getAdapterPosition());
        }
        else {
            mItemOrChildClick.onClickItemChild(getAdapter(),getAdapterPosition(),v);
        }
    }

    protected void onCheckChanged(View v, boolean checked) {
        if (null == mItemCheckChange) return;
        mItemCheckChange.onCheckChange(getAdapter(),getAdapterPosition(),checked,v);
    }
}
