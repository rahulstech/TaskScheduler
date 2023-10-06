package rahulstech.android.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rahulstech.android.ui.listener.OnClickItemOrChildListener;
import rahulstech.android.ui.listener.OnItemOrChildCheckChangeListener;

@SuppressWarnings("unused")
public abstract class ClickableViewHolder<I> extends BaseViewHolder<I> {

    private OnClickItemOrChildListener mItemOrChildClick;

    private OnItemOrChildCheckChangeListener mItemCheckChange;

    public ClickableViewHolder(@NonNull RecyclerView.Adapter<?> adapter, @NonNull View itemView) {
        super(itemView);
        setAdapter(adapter);
    }

    public void setOnClickItemOrChildClickListener(OnClickItemOrChildListener listener) {
        mItemOrChildClick = listener;
    }

    public OnClickItemOrChildListener getOnClickItemOrChildListener() {
        return mItemOrChildClick;
    }

    public OnItemOrChildCheckChangeListener getOnItemCheckChangeListener() {
        return mItemCheckChange;
    }

    public void setOnItemOrChildCheckChangeListener(OnItemOrChildCheckChangeListener listener) {
        mItemCheckChange = listener;
    }

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
