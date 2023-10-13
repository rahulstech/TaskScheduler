package rahulstech.android.taskschedulder.listener;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface OnClickItemOrChildListener {

    void onClickItem(@NonNull RecyclerView.Adapter<?> adapter, int position);

    void onClickItemChild(@NonNull RecyclerView.Adapter<?> adapter, int position, @NonNull View child);
}
