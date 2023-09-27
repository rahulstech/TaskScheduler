package rahulstech.android.ui.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("unused")
public class RecyclerViewItemListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetector mGesturesDetector;
    private final RecyclerView mRecyclerView;

    private OnItemClickListener mClickListener;

    public RecyclerViewItemListener(@NonNull RecyclerView rv) {
        mRecyclerView = rv;
        GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return onClickItem(e);
            }
        };
        mGesturesDetector = new GestureDetector(rv.getContext(), mGestureListener);
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return mGesturesDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    private boolean onClickItem(@NonNull MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        View itemView = mRecyclerView.findChildViewUnder(x,y);
        if (null == itemView) return false;
        int adapterPosition = mRecyclerView.getChildAdapterPosition(itemView);
        if (null != mClickListener) {
            mClickListener.onItemClick(mRecyclerView,adapterPosition);
        }
        return true;
    }
}
